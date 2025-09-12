package com.example.SellPhone.Service;

import com.example.SellPhone.DTO.Respone.ShoppingCart.CartItemRespone;
import com.example.SellPhone.Entity.*;
import com.example.SellPhone.Repository.ProductRepository;
import com.example.SellPhone.Repository.ShoppingCartRepository;
import com.example.SellPhone.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    @Transactional
    public void addItemToCart(Long userId, Long productId, int quantity, int rom) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        ShoppingCart cart = shoppingCartRepository.findByUser(user)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(user);
                    return shoppingCartRepository.save(newCart);
                });

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId) && Objects.equals(item.getRom(), rom))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(productRepository.findById(productId).orElseThrow());
            newItem.setQuantity(quantity);
            newItem.setRom(rom);
            newItem.setShoppingCart(cart);
            cart.getItems().add(newItem);
        }

        shoppingCartRepository.save(cart);
    }

    public List<CartItemRespone> getCartItemsByUserId(Long userId) {
         ShoppingCart cart = shoppingCartRepository.findByUser_UserId(userId);
        if (cart == null) {
            return Collections.emptyList();
        }

        List<CartItemRespone> cartItemDtos = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            Specification spec = product.getSpecification();

            // Tìm variant phù hợp rom + specificationId
            SpecificationVariant variant = spec.getVariants().stream()
                    .filter(v -> v.getRom().equals(item.getRom())
                            && v.getSpecification().getSpecificationId().equals(product.getSpecification().getSpecificationId()))
                    .findFirst()
                    .orElse(null);

            if (variant != null) {
                CartItemRespone dto = new CartItemRespone(
                        item.getCartItemId(),
                        product.getProductId(),
                        product.getName(),
                        product.getImageUrl(),
                        item.getRom(),
                        product.getColor(),
                        variant.getSellingPrice(),
                        item.getQuantity(),
                        variant.getQuantity()
                );
                cartItemDtos.add(dto);
            }
        }

        return cartItemDtos;
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @Transactional
    public void updateCartItemQuantity(Long userId, Long cartItemId, int newQuantity) {
        ShoppingCart cart = shoppingCartRepository.findByUser_UserId(userId);
        if(cart == null) {
            throw new NoSuchElementException("Không tìm thấy giỏ hàng của người dùng");
        }
        else{
            Optional<CartItem> itemOpt = cart.getItems().stream()
                    .filter(item -> item.getCartItemId().equals(cartItemId))
                    .findFirst();

            if (itemOpt.isPresent()) {
                CartItem item = itemOpt.get();
                item.setQuantity(newQuantity);
                shoppingCartRepository.save(cart);
            } else {
                throw new NoSuchElementException("Không tìm thấy sản phẩm trong giỏ hàng");
            }
        }
    }

    @Transactional
    public void deleteCartItemById(Long userId, Long cartItemId) {
        ShoppingCart cart = shoppingCartRepository.findByUser_UserId(userId);
        if (cart == null) {
            throw new NoSuchElementException("Không tìm thấy giỏ hàng của người dùng");
        }

        boolean removed = cart.getItems().removeIf(item -> item.getCartItemId().equals(cartItemId));

        if (!removed) {
            throw new NoSuchElementException("Không tìm thấy sản phẩm trong giỏ hàng");
        }

        // Do trong ShoppingCart entity đã có orphanRemoval = true nên chỉ cần save lại cart
        shoppingCartRepository.save(cart);
    }
}
