package com.selimhorri.app.mapper;

import org.springframework.stereotype.Component;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;

@Component
public class UserMapper {
    
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        
        UserDto userDto = UserDto.builder()
            .userId(user.getUserId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .imageUrl(user.getImageUrl())
            .build();

        if (user.getCredential() != null) {
            CredentialDto credentialDto = CredentialDto.builder()
                .credentialId(user.getCredential().getCredentialId())
                .username(user.getCredential().getUsername())
                .password(user.getCredential().getPassword())
                .roleBasedAuthority(user.getCredential().getRoleBasedAuthority())
                .isEnabled(user.getCredential().getIsEnabled())
                .isAccountNonExpired(user.getCredential().getIsAccountNonExpired())
                .isAccountNonLocked(user.getCredential().getIsAccountNonLocked())
                .isCredentialsNonExpired(user.getCredential().getIsCredentialsNonExpired())
                .build();
            userDto.setCredentialDto(credentialDto);
        }
        
        return userDto;
    }
    
    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        
        User user = User.builder()
            .userId(userDto.getUserId())
            .firstName(userDto.getFirstName())
            .lastName(userDto.getLastName())
            .email(userDto.getEmail())
            .phone(userDto.getPhone())
            .imageUrl(userDto.getImageUrl())
            .build();

        if (userDto.getCredentialDto() != null) {
            Credential credential = Credential.builder()
                .credentialId(user.getUserId())
                .username(userDto.getCredentialDto().getUsername())
                .password(userDto.getCredentialDto().getPassword())
                .roleBasedAuthority(userDto.getCredentialDto().getRoleBasedAuthority())
                .isEnabled(userDto.getCredentialDto().getIsEnabled())
                .isAccountNonExpired(userDto.getCredentialDto().getIsAccountNonExpired())
                .isAccountNonLocked(userDto.getCredentialDto().getIsAccountNonLocked())
                .isCredentialsNonExpired(userDto.getCredentialDto().getIsCredentialsNonExpired())
                .user(user)
                .build();
            user.setCredential(credential);
        }
        
        return user;
    }
} 