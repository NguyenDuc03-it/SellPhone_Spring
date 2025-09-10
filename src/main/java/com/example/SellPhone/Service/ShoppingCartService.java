package com.example.SellPhone.Service;

import com.example.SellPhone.Entity.CartItem;
import com.example.SellPhone.Entity.ShoppingCart;
import com.example.SellPhone.Entity.User;
import com.example.SellPhone.Repository.ProductRepository;
import com.example.SellPhone.Repository.ShoppingCartRepository;
import com.example.SellPhone.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShoppingCartService {

    ShoppingCartRepository shoppingCartRepository;
    UserRepository userRepository;
    ProductRepository productRepository;

    public int countTotalCartItems(Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUser_UserId(userId);
        if (cart == null || cart.getItems() == null) return 0;

        return cart.getItems().size();
    }

    // Thêm sản phẩm vào giỏ hàng
    public void addItemToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        ShoppingCart cart = shoppingCartRepository.findByUser(user)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(user);
                    return shoppingCartRepository.save(newCart);
                });

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(productRepository.findById(productId).orElseThrow());
            newItem.setQuantity(quantity);
            newItem.setShoppingCart(cart);
            cart.getItems().add(newItem);
        }

        shoppingCartRepository.save(cart);
    }
}
