package com.example.gildedrose.service;

import com.example.gildedrose.model.Item;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    private final InventoryService inventoryService = new InventoryService();

    @Test
    void updatesCorrectly() {
        List<Item> input = List.of(
                new Item("Aged Brie", 1, 1),
                new Item("Backstage passes", -1, 2),
                new Item("Backstage passes", 9, 2),
                new Item("Sulfuras", 0, 80),
                new Item("Normal Item", -1, 55),
                new Item("Normal Item", 2, 2),
                new Item("Conjured", 2, 2),
                new Item("Conjured", -1, 5)
        );

        List<Item> expected = List.of(
                new Item("Aged Brie", 0, 2),
                new Item("Backstage passes", -2, 0),
                new Item("Backstage passes", 8, 4),
                new Item("Sulfuras", 0, 80),
                new Item("Normal Item", -2, 50),
                new Item("Normal Item", 1, 1),
                new Item("Conjured", 1, 0),
                new Item("Conjured", -2, 1)
        );

        List<Item> actual = inventoryService.updateInventory(input);

        assertEquals(expected, actual);
    }


    @Test
    void normalItemUsesStandardDegradationWhenSellInIsZeroAtStartOfDay() {
        Item input = new Item("Normal Item", 0, 5);

        Item expected = new Item("Normal Item", -1, 4);

        List<Item> actual = inventoryService.updateInventory(List.of(input));

        assertEquals(expected, actual.get(0));
    }

    @Test
    void agedBrieUsesStandardIncreaseWhenSellInIsZeroAtStartOfDay() {
        Item input = new Item("Aged Brie", 0, 10);

        Item expected = new Item("Aged Brie", -1, 11);

        List<Item> actual = inventoryService.updateInventory(List.of(input));

        assertEquals(expected, actual.get(0));
    }

    @Test
    void normalItemDegradesTwiceWhenAlreadyPastSellByDate() {
        Item input = new Item("Normal Item", -1, 10);

        Item expected = new Item("Normal Item", -2, 8);

        List<Item> actual = inventoryService.updateInventory(List.of(input));

        assertEquals(expected, actual.get(0));
    }

    @Test
    void agedBrieIncreasesTwiceWhenAlreadyPastSellByDate() {
        Item input = new Item("Aged Brie", -1, 10);

        Item expected = new Item("Aged Brie", -2, 12);

        List<Item> actual = inventoryService.updateInventory(List.of(input));

        assertEquals(expected, actual.get(0));
    }

    @Test
    void unknownItemNamesAreTreatedAsNormalItems() {
        Item input = new Item("Unrecognised Item", 3, 10);

        Item expected = new Item("Unrecognised Item", 2, 9);

        List<Item> actual = inventoryService.updateInventory(List.of(input));

        assertEquals(expected, actual.get(0));
    }

    @Test
    void backstagePassesUseCorrectThresholdsAndExpireAfterConcert() {
        List<Item> input = List.of(
                new Item("Backstage passes", 11, 10),
                new Item("Backstage passes", 10, 10),
                new Item("Backstage passes", 6, 10),
                new Item("Backstage passes", 5, 10),
                new Item("Backstage passes", 0, 10),
                new Item("Backstage passes", -1, 10)
        );

        List<Item> expected = List.of(
                new Item("Backstage passes", 10, 11),
                new Item("Backstage passes", 9, 12),
                new Item("Backstage passes", 5, 12),
                new Item("Backstage passes", 4, 13),
                new Item("Backstage passes", -1, 0),
                new Item("Backstage passes", -2, 0)
        );

        assertEquals(expected, inventoryService.updateInventory(input));
    }

    @Test
    void conjuredItemsDegradeTwiceAsFastAsNormalItems() {
        List<Item> input = List.of(
                new Item("Conjured", 2, 10),
                new Item("Conjured", 0, 10),
                new Item("Conjured", -1, 10)
        );

        List<Item> expected = List.of(
                new Item("Conjured", 1, 8),
                new Item("Conjured", -1, 8),
                new Item("Conjured", -2, 6)
        );

        assertEquals(expected, inventoryService.updateInventory(input));
    }


    @Test
    void sulfurasNeverChanges() {
        Item input = new Item("Sulfuras", 0, 80);

        List<Item> actual = inventoryService.updateInventory(List.of(input));

        assertEquals(input, actual.get(0));
    }

    @Test
    void qualityAlwaysStaysWithinAllowedRange() {
        List<Item> input = List.of(
                new Item("Normal Item", 2, 0),
                new Item("Aged Brie", 1, 50),
                new Item("Backstage passes", 5, 49)
        );

        List<Item> expected = List.of(
                new Item("Normal Item", 1, 0),
                new Item("Aged Brie", 0, 50),
                new Item("Backstage passes", 4, 50)
        );

        assertEquals(expected, inventoryService.updateInventory(input));
    }

    @Test
    void emptyInventoryReturnsEmptyInventory() {
        List<Item> actual = inventoryService.updateInventory(List.of());

        assertTrue(actual.isEmpty());
    }

    @Test
    void rejectsNullInventory() {
        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.updateInventory(null)
        );
    }

    @Test
    void rejectsNullItems() {
        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.updateInventory(
                        Collections.singletonList(null)
                )
        );
    }

    @Test
    void rejectsBlankItemNames() {
        List<Item> input = List.of(
                new Item(" ", 1, 10)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.updateInventory(input)
        );
    }

    @Test
    void doesNotModifyOriginalInventory() {
        List<Item> input = new ArrayList<>(
                List.of(new Item("Normal Item", 2, 5))
        );

        List<Item> original = new ArrayList<>(input);

        List<Item> actual = inventoryService.updateInventory(input);

        assertEquals(original, input);
        assertNotSame(input, actual);
    }
}