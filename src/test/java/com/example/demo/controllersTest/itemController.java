package com.example.demo.controllersTest;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class itemController {


    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;





    public static List<Item> createListOfItems() {

        List<Item> itemList = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            Item item = new Item();
            item.setId((long) i);

            BigDecimal bigDecimal = BigDecimal.valueOf(i * 1.2);
            item.setPrice(bigDecimal);

            item.setName("Item " + item.getId());
            item.setDescription("Desc ");

            itemList.add(item);
        }

        return itemList;
    }

    @Before
    public void setup(){

        when(itemRepository.findById(1L)).thenReturn(Optional.of(create_Item(1)));
        when(itemRepository.findAll()).thenReturn(createListOfItems());
        when(itemRepository.findByName("item")).thenReturn(Arrays.asList(create_Item(1), create_Item(2)));

    }

    @Test
    public void getItems(){
        ResponseEntity<List<Item>> res = itemController.getItems();

        assertEquals(200, res.getStatusCodeValue());
        List<Item> items = res.getBody();

        assertEquals(createListOfItems(), items);

        verify(itemRepository , times(1)).findAll();
    }

    public static Item create_Item(long id){
        Item item = new Item();
        item.setId(id);

        BigDecimal bigDecimal = BigDecimal.valueOf(id * 1.2);
        item.setPrice(bigDecimal);

        item.setName("Item " + item.getId());

        return item;
    }

    @Test
    public void getItemById(){

        ResponseEntity<Item> res = itemController.getItemById(1L);

        assertEquals(200, res.getStatusCodeValue());

        Item item = res.getBody();
        assertEquals(create_Item(1L), item);
        verify(itemRepository, times(1)).findById(1L);
    }
    @Test
    public void getItemById_Invalid(){

        ResponseEntity<Item> res = itemController.getItemById(5L);

        assertEquals(404, res.getStatusCodeValue());

        assertNull(res.getBody());
        verify(itemRepository, times(1)).findById(5L);
    }
    //
    @Test
    public void getItemByName(){
        ResponseEntity<List<Item>> res = itemController.getItemsByName("item");
        assertEquals(200, res.getStatusCodeValue());
        List<Item> items = Arrays.asList(create_Item(1), create_Item(2));

        assertEquals(createListOfItems(), items);

        verify(itemRepository , times(1)).findByName("item");
    }

    @Test
    public void getItemByName_Invalid(){
        ResponseEntity<List<Item>> res = itemController.getItemsByName("invalid");


        assertEquals(404, res.getStatusCodeValue());

        assertNull(res.getBody());

        verify(itemRepository , times(1)).findByName("invalid");
    }






}
