package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.impl.UserServiceImpl;
import com.selimhorri.app.mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private User testUser;
    private UserDto testUserDto;
    private Credential testCredential;
    private CredentialDto testCredentialDto;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhone("1234567890");
        
        testCredential = new Credential();
        testCredential.setCredentialId(1);
        testCredential.setUsername("johndoe");
        testCredential.setPassword("password123");
        testCredential.setRoleBasedAuthority(RoleBasedAuthority.ROLE_USER);
        testCredential.setIsEnabled(true);
        testCredential.setIsAccountNonExpired(true);
        testCredential.setIsAccountNonLocked(true);
        testCredential.setIsCredentialsNonExpired(true);
        testCredential.setUser(testUser);
        
        testUser.setCredential(testCredential);
        
        testUserDto = new UserDto();
        testUserDto.setUserId(1);
        testUserDto.setFirstName("John");
        testUserDto.setLastName("Doe");
        testUserDto.setEmail("john.doe@example.com");
        testUserDto.setPhone("1234567890");
        
        testCredentialDto = new CredentialDto();
        testCredentialDto.setCredentialId(1);
        testCredentialDto.setUsername("johndoe");
        testCredentialDto.setPassword("password123");
        testCredentialDto.setRoleBasedAuthority(RoleBasedAuthority.ROLE_USER);
        testCredentialDto.setIsEnabled(true);
        testCredentialDto.setIsAccountNonExpired(true);
        testCredentialDto.setIsAccountNonLocked(true);
        testCredentialDto.setIsCredentialsNonExpired(true);
        testCredentialDto.setUserDto(testUserDto);
        
        testUserDto.setCredentialDto(testCredentialDto);
        
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);
        when(userMapper.toEntity(testUserDto)).thenReturn(testUser);
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        // Act
        List<UserDto> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
        assertNotNull(result.get(0).getCredentialDto());
        assertEquals(testUser.getCredential().getUsername(), result.get(0).getCredentialDto().getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // Act
        UserDto result = userService.findById(1);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertNotNull(result.getCredentialDto());
        assertEquals(testUser.getCredential().getUsername(), result.getCredentialDto().getUsername());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void findById_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.findById(999));
        verify(userRepository, times(1)).findById(999);
    }

    @Test
    void save_ShouldReturnSavedUser() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDto result = userService.save(testUserDto);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertNotNull(result.getCredentialDto());
        assertEquals(testUser.getCredential().getUsername(), result.getCredentialDto().getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        UserDto result = userService.findByEmail("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertNotNull(result.getCredentialDto());
        assertEquals(testUser.getCredential().getUsername(), result.getCredentialDto().getUsername());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    // Nuevas pruebas unitarias

    @Test
    void save_ShouldThrowException_WhenUserEmailAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail(testUserDto.getEmail())).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.save(testUserDto));
        verify(userRepository, times(1)).findByEmail(testUserDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void save_ShouldCreateNewUser_WhenEmailDoesNotExist() {
        // Arrange
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Modificar el email del DTO para la prueba
        testUserDto.setEmail("new@example.com");

        // Act
        UserDto result = userService.save(testUserDto);

        // Assert
        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("new@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findByEmail_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.findByEmail("nonexistent@example.com"));
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<UserDto> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void save_ShouldMaintainCredentialUserRelationship() {
        // Arrange
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertNotNull(savedUser.getCredential(), "La credencial no debe ser nula");
            assertEquals(savedUser.getUserId(), savedUser.getCredential().getCredentialId(), 
                "El ID de la credencial debe ser igual al ID del usuario");
            return savedUser;
        });

        // Act
        UserDto result = userService.save(testUserDto);

        // Assert
        assertNotNull(result, "El resultado no debe ser nulo");
        assertNotNull(result.getCredentialDto(), "La credencial del resultado no debe ser nula");
        assertEquals(result.getUserId(), result.getCredentialDto().getCredentialId(), 
            "El ID de la credencial debe ser igual al ID del usuario");
        verify(userRepository, times(1)).save(any(User.class));
    }
} 