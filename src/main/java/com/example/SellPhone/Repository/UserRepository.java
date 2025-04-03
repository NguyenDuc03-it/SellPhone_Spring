package com.example.SellPhone.Repository;

import com.example.SellPhone.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByRole(String role, Pageable pageable);

    Optional<User> findByEmail(String email);
}
