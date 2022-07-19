package com.example.demo.controllersTest;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
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

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class cartController{

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;




    public static Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setId(1L);
        List<Item> items = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            items.add(createItem(i));
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

    public static Item createItem(long id){
        Item item = new Item();
        item.setId(id);
        BigDecimal bigDecimal = BigDecimal.valueOf(id * 1.2);
        item.setPrice(bigDecimal);

        item.setName("Item " + item.getId());

        item.setDescription("Desc ");
        return item;
    }

    
    @Before
    public void setup(){

        when(userRepository.findByUsername("root")).thenReturn(createUser());
        when(itemRepository.findById(any())).thenReturn(Optional.of(createItem(1)));

    }
    public static User createUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("root");
        user.setPassword("password");
        user.setCart(createCart(user));

        return user;
    }

    @Test
    public void remove_FromCart(){

        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("root");
        req.setQuantity(2);
        req.setItemId(1);

        ResponseEntity<Cart> res = cartController.removeFromcart(req);
        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        Cart actualCart = res.getBody();
        Cart generatedCart = createCart(createUser());
        assertNotNull(actualCart);

        Item item = createItem(req.getItemId());
        BigDecimal itemPrice = item.getPrice();


        BigDecimal multiply = itemPrice.multiply(BigDecimal.valueOf(req.getQuantity()));
        BigDecimal expectedTotal = generatedCart.getTotal().subtract(multiply);

        assertEquals("root", actualCart.getUser().getUsername());
        assertEquals(createItem(2), actualCart.getItems().get(0));
        assertEquals(expectedTotal, actualCart.getTotal());

        verify(cartRepository, times(1)).save(actualCart);

    }

    @Test
    public void Invalid_Username(){

        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("test");
        req.setQuantity(1);
        req.setItemId(1);


        ResponseEntity<Cart> removeFromcart = cartController.removeFromcart(req);
        assertNotNull(removeFromcart);

        assertEquals(404, removeFromcart.getStatusCodeValue());
        ResponseEntity<Cart> addTocart = cartController.addTocart(req);
        assertNotNull(addTocart);
        assertEquals(404, addTocart.getStatusCodeValue());

        verify(userRepository, times(2)).findByUsername("test");

    }


    @Test
    public void addToCart(){
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("root");
        req.setQuantity(3);
        req.setItemId(1);


        ResponseEntity<Cart> res = cartController.addTocart(req);
        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        Cart actualCartresponse = res.getBody();
        Cart generatedCartresponse = createCart(createUser());

        assertNotNull(actualCartresponse);
        Item item = createItem(req.getItemId());
        BigDecimal itemPrice = item.getPrice();


        BigDecimal bigDecimal = BigDecimal.valueOf(req.getQuantity());
        BigDecimal expectedTotal = itemPrice.multiply(bigDecimal).add(generatedCartresponse.getTotal());

        assertEquals("root", actualCartresponse.getUser().getUsername());

        assertEquals(generatedCartresponse.getItems().size() + req.getQuantity(), actualCartresponse.getItems().size());
        assertEquals(expectedTotal, actualCartresponse.getTotal());

        verify(cartRepository, times(1)).save(actualCartresponse);

    }





}