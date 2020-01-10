package com.gmail.buer2012.utils;

import com.gmail.buer2012.entity.RoleName;
import com.gmail.buer2012.entity.User;
import com.gmail.buer2012.security.UserPrincipal;
import com.gmail.buer2012.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
@AllArgsConstructor
public class ApiUtils {
    private UserService userService;
    
    public RoleName getRoleFromAuthorities(Collection<? extends GrantedAuthority> authorities) {
        String role = String.valueOf(Arrays.stream(authorities.toArray()).findFirst().orElse(null));
        return RoleName.valueOf(role);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return userService.findById(principal.getId()).get();
    }
}
