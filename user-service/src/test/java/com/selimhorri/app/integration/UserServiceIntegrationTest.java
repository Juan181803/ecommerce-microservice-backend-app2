package com.selimhorri.app.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.mapper.UserMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;
    
    private User testUser;
    private UserDto testUserDto;
    private Credential testCredential;
    private CredentialDto testCredentialDto;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
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
        
        testUserDto = userMapper.toDto(testUser);
    }
    
    @Test
    void createUser_ShouldCreateUserAndCredential() throws Exception {
        // Given
        String userJson = objectMapper.writeValueAsString(testUserDto);
        
        // When
        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        // Then
        UserDto createdUser = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertNotNull(createdUser);
        assertEquals(testUserDto.getEmail(), createdUser.getEmail());
        assertNotNull(createdUser.getCredentialDto());
        assertEquals(testUserDto.getCredentialDto().getUsername(), createdUser.getCredentialDto().getUsername());
    }
    
    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        // Given
        User savedUser = userRepository.save(testUser);
        
        // When
        MvcResult result = mockMvc.perform(get("/api/users/{id}", savedUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        // Then
        UserDto foundUser = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertNotNull(foundUser);
        assertEquals(savedUser.getUserId(), foundUser.getUserId());
    }
    
    @Test
    void updateUser_ShouldUpdateUserAndCredential() throws Exception {
        // Given
        User savedUser = userRepository.save(testUser);
        testUserDto.setFirstName("Updated");
        testUserDto.getCredentialDto().setUsername("updateduser");
        String userJson = objectMapper.writeValueAsString(testUserDto);
        
        // When
        MvcResult result = mockMvc.perform(put("/api/users/{id}", savedUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        // Then
        UserDto updatedUser = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertNotNull(updatedUser);
        assertEquals("Updated", updatedUser.getFirstName());
        assertEquals("updateduser", updatedUser.getCredentialDto().getUsername());
    }
    
    @Test
    void deleteUser_ShouldDeleteUserAndCredential() throws Exception {
        // Given
        User savedUser = userRepository.save(testUser);
        
        // When
        mockMvc.perform(delete("/api/users/{id}", savedUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        // Then
        assertFalse(userRepository.existsById(savedUser.getUserId()));
    }
    
    @Test
    void getUserByEmail_ShouldReturnUser() throws Exception {
        // Given
        User savedUser = userRepository.save(testUser);
        
        // When
        MvcResult result = mockMvc.perform(get("/api/users/email/{email}", savedUser.getEmail())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        // Then
        UserDto foundUser = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertNotNull(foundUser);
        assertEquals(savedUser.getEmail(), foundUser.getEmail());
    }
} 