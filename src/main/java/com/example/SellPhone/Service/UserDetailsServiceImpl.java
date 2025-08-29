package com.example.SellPhone.Service;

import com.example.SellPhone.Config.CustomUserDetails;
import com.example.SellPhone.Entity.User;
import com.example.SellPhone.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;


// Lấy thông tin người dùng
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại"));

        if("Hoạt động".equals(user.getStatus())){
            return new CustomUserDetails(
                    user.getUserId(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getFullname(),
                    getAuthorities(user)
            );
        }
        else{
            System.out.println("Tài khoản đã bị khóa");
            throw new UsernameNotFoundException("Tài khoản đã bị khóa");

        }

    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRoleWithPrefix()));
    }
}
