package com.spartans.service;

import com.spartans.exception.BookNotAvailableException;
import com.spartans.model.Book;
import com.spartans.model.BookInventory;
import com.spartans.repository.BookInventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {

    @Mock
    private BookInventoryRepository inventoryRepository;

    @InjectMocks
    private BookInventoryServiceImpl inventoryService;

    private BookInventory inventory;
    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setBookId(100L);

        inventory = new BookInventory();
        inventory.setInventoryId(1L);
        inventory.setBook(book);
        inventory.setAvailableCopies(3);
        inventory.setTotalCopies(5);
    }

    @Test
    void testIsAvailable_true() {
        Mockito.when(inventoryRepository.findByBookId(100L)).thenReturn(Optional.of(inventory));

        Assertions.assertTrue(inventoryService.isAvailable(100L));
    }

    @Test
    void testDecrementStock_success() {
        Mockito.when(inventoryRepository.findByBookId(100L)).thenReturn(Optional.of(inventory));
        inventoryService.decrementStock(100L);

        Assertions.assertEquals(2, inventory.getAvailableCopies());
        Mockito.verify(inventoryRepository).save(inventory);
    }

    @Test
    void testDecrementStock_outOfStock() {
        inventory.setAvailableCopies(0);
        Mockito.when(inventoryRepository.findByBookId(100L)).thenReturn(Optional.of(inventory));

        Assertions.assertThrows(BookNotAvailableException.class, () -> {
            inventoryService.decrementStock(100L);
        });
    }

    @Test
    void testIncrementStock() {
        Mockito.when(inventoryRepository.findByBookId(100L)).thenReturn(Optional.of(inventory));
        inventoryService.incrementStock(100L);

        Assertions.assertEquals(4, inventory.getAvailableCopies());
        Mockito.verify(inventoryRepository).save(inventory);
    }
}