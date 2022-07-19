package com.example.demo.controllersTest;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class orderController {


    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;





    //    User user = new User();
//        user.setId(1);
//        user.setUsername("root");
//        user.setPassword("password");
    public static User User_CreateService() {
        User user = new User();
        user.setId(1);
        user.setUsername("root");
        user.setPassword("password");


        //user.setPas


        Cart cart = new Cart();
        cart.setId(1L);
        List<Item> items = new ArrayList<>();
//
        for (int i = 1; i <= 2; i++) {
            items.add(createItem_Service(i));
        }
        cart.setItems(items);
        boolean seen = false;
        BigDecimal acc = null;
        for (Item item : items) {
            BigDecimal price = item.getPrice();
            if (!seen) {
                seen = true;
                acc = price;
            } else {
                acc = acc.add(price);
            }
        }
        if (seen) cart.setTotal(Optional.of(acc).get());
        else cart.setTotal(Optional.<BigDecimal>empty().get());
        cart.setUser(user);
        user.setCart(cart);

        return user;
    }

    public static Cart creating_CartService(User user) {
        Cart cart = new Cart();
        cart.setId(1L);
        List<Item> items = new ArrayList<>();
//
        for (int i = 1; i <= 2; i++) {
            items.add(createItem_Service(i));
        }

        cart.setItems(items);
        boolean seen = false;
        BigDecimal acc = null;
        for (Item item : items) {
            BigDecimal price = item.getPrice();
            if (!seen) {
                seen = true;
                acc = price;
            } else {
                acc = acc.add(price);
            }
        }
        if (seen) cart.setTotal(Optional.of(acc).get());
        else cart.setTotal(Optional.<BigDecimal>empty().get());
        cart.setUser(user);

        return cart;
    }


    public static Item createItem_Service(long id){
        Item item = new Item();
        item.setId(id);

        item.setPrice(BigDecimal.valueOf(id * 1.2));

        item.setName("Item " + item.getId());
        item.setDescription("Desc ");
        return item;
    }

    public static List<UserOrder> createOrders(){
        List<UserOrder> orders = new ArrayList<>();

        IntStream.range(0,2).forEach(i -> {
            UserOrder order = new UserOrder();


            //
            User user = new User();

            user.setId(1);
            user.setUsername("root");
            user.setPassword("password");
            //user.setPas
            user.setCart(creating_CartService(user));

            Cart cart = creating_CartService(user);


            order.setItems(cart.getItems());
            order.setTotal(cart.getTotal());
            order.setUser(User_CreateService());
            order.setId(Long.valueOf(i));

            orders.add(order);
        });
        return orders;
    }

    @Before
    public void setup(){
        User user = User_CreateService();

        when(userRepository.findByUsername("root")).thenReturn(user);
        when(orderRepository.findByUser(any())).thenReturn(createOrders());
    }
    @Test
    public void getOrdersForUser(){

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("root");

        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> orders = response.getBody();

        int size = createOrders().size();
        assertEquals(size , orders.size());

    }

    @Test
    public void Sucess_Submit(){

        ResponseEntity<UserOrder> response = orderController.submit("root");

        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();

        List<Item> items = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            items.add(createItem_Service(i));
        }
        List<Item> orderItems = order.getItems();

        assertEquals(items,orderItems );
        assertEquals(User_CreateService().getId(), order.getUser().getId());


        verify(orderRepository, times(1)).save(order);

    }

    @Test
    public void SubmitInvalid(){

        ResponseEntity<UserOrder> res = orderController.submit("invalid username");

        assertEquals(404, res.getStatusCodeValue());
        assertNull( res.getBody());

        verify(userRepository, times(1)).findByUsername("invalid username");
    }





}
