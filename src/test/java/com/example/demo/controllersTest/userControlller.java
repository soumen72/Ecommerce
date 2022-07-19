package com.example.demo.controllersTest;

import com.example.demo.TestUtils_Injections;
import com.example.demo.controllers.Exceptions.RuntimeExceptionHandle;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class userControlller{



    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils_Injections.injectObjects(userController, "userRepository", userRepository);
        //TestUtils_Injections.injectObjects(userController, "cartRepository", userRepository);
        TestUtils_Injections.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils_Injections.injectObjects(userController, "bCryptPasswordEncoder", encoder);

    }

    @Test
    public void findbyUserId() {
        User userobj = new User();
        userobj.setId(1);
        userobj.setUsername("root");

        Optional<User> userobj1 = Optional.of(userobj);
        when(userRepository.findById(anyLong())).thenReturn(userobj1);

        final ResponseEntity<User> response = userController.findById(1L);


//        User user = response.getBody();
//        assertNotNull(user);
//        assertEquals(1, user.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("root", user.getUsername());
    }

    @Test
    public void createUserHappyPath() {
        when(encoder.encode(anyString())).thenReturn("thisIsHashed");

        CreateUserRequest createUser_Req= new CreateUserRequest();
        createUser_Req.setUsername("root");
        createUser_Req.setPass("Password");
        createUser_Req.setConfirmPass("Password");

        ResponseEntity<User> response;
        try {
            response = userController.createUser(createUser_Req);
            assertNotNull(response);
            assertEquals(200, response.getStatusCodeValue());

            User user = response.getBody();
            assertNotNull(user);

            assertEquals("root", user.getUsername());


        } catch (RuntimeExceptionHandle e) {
            e.printStackTrace();
        }


    }

    @Test
    public void CreateUser_passNotValid() {
        when(encoder.encode(anyString())).thenReturn("thisIsHashed");
        CreateUserRequest createUser_Request = new CreateUserRequest();
        createUser_Request.setUsername("root");
        createUser_Request.setPass("123456");
        createUser_Request.setConfirmPass("123456");


        try
        {
            //Run exception throwing operation
        }
        catch(RuntimeException re)
        {
            String message = "Error with user password";
            assertEquals(message, re.getMessage());
            //return;
            throw re;
        }

    }

    @Test
    public void findByUserName() {
        User user = new User();
        
        user.setUsername("root");
        
        
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        final ResponseEntity<User> response = userController.findByUserName("root");
        assertNotNull(response);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        User user1 = response.getBody();
        assertEquals(0, user1.getId());
        assertEquals("root", user1.getUsername());
    }

    @Test
    public void createUser_ifUserExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(new User());
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPass("1234567");
        createUserRequest.setConfirmPass("1234567");

        try
        {
            //Run exception throwing operation
        }
        catch(RuntimeException re)
        {
            String message = "username already exist";
            assertEquals(message, re.getMessage());
            //return;
            throw re;
        }

    }



}
