package com.userauthenticationmicroservice.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.userauthenticationmicroservice.models.Status;
import com.userauthenticationmicroservice.models.User;

import lombok.Getter;


//Creating this custom user details class to implement required implementation of UserDetails interface for Spring security to work with our User entity. This class will be used by the UserDetailsService implementation to load user-specific data during authentication.
//Made a separate wrapper class instead of implementing UserDetails in User entity for-
//Separation of concerns 
// Avoid exposing internal entity structure 
// Flexibility for multiple authentication sources 
// Security abstraction
@Getter
public class CustomUserDetails implements UserDetails{
    private User user;

    public CustomUserDetails(User user){
        this.user = user;
    }

    public CustomUserDetails getNewCustomUserDetails(User user){
        return new CustomUserDetails(user);
    }
     
    //Spring security expects the Username field to be the unique identifier for authentication. In this application, that is the email field.
    @Override
    public String getUsername(){
        return user.getEmail();
    }

    @Override
    public String getPassword(){
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getValue()))
                .toList();
    }

    @Override
    public boolean isAccountNonExpired(){
        return user.getStatus() != Status.DELETED;
    }

    @Override
    public boolean isAccountNonLocked(){
        return user.getStatus() != Status.SUSPENDED;
    }

    @Override
    public boolean isEnabled(){
        //If user is marked as INACTIVE due to inactivity, they should still be able to log in and their status will be set back to ACTIVE upon successful login. So we only return false for isEnabled() if user's status is EMAIL_NOT_VERIFIED, meaning they haven't verified their email yet and shouldn't be allowed to log in until they do so.
        return user.getStatus() != Status.EMAIL_NOT_VERIFIED;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true; //Assuming password never expires. Change this if password expiration logic is implemented.
    }
}
