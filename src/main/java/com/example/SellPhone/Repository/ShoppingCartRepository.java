package com.example.SellPhone.Repository;

import com.example.SellPhone.Entity.ShoppingCart;
import com.example.SellPhone.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    ShoppingCart findByUser_UserId(Long userId);

    Optional<ShoppingCart> findByUser(User user);
}
