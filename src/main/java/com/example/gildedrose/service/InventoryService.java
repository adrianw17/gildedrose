package com.example.gildedrose.service;

import com.example.gildedrose.model.Item;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryService {

    public List<Item> updateInventory(List<Item> items) {

        if (items == null) {
            throw new IllegalArgumentException("Inventory must not be null");
        }

        List<Item> updatedInventory = new ArrayList<>();

        for (Item item : items) {
            updatedInventory.add(updateItem(item));
        }

        return updatedInventory;
    }

    private Item updateItem(Item item) {

        if (item == null) {
            throw new IllegalArgumentException(
                    "Inventory must not contain null items"
            );
        }

        if (item.name() == null || item.name().isBlank()) {
            throw new IllegalArgumentException("Item name must not be blank");
        }

        // Sulfuras never changes
        if ("Sulfuras".equals(item.name())) {
            return item;
        }

        // Calculate SellIn first
        int updatedSellIn = item.sellIn() - 1;

        // Calculate quality using the updated SellIn
        int updatedQuality = calculateUpdatedQuality(item, updatedSellIn);

        // Quality restrictions
        updatedQuality = Math.max(0, Math.min(50, updatedQuality));

        return new Item(item.name(), updatedSellIn, updatedQuality);
    }

    private int calculateUpdatedQuality(Item item, int updatedSellIn) {

        String name = item.name();
        int quality = item.quality();
        int sellIn = item.sellIn();

        if ("Aged Brie".equals(name)) {

            // Increases by 1 normally, or 2 if already past sell-by date
            return quality + (sellIn < 0 ? 2 : 1);

        } else if ("Backstage passes".equals(name)) {

            // Quality drops to 0 after the concert
            if (updatedSellIn < 0) {
                return 0;
            } else if (sellIn <= 5) {
                return quality + 3;
            } else if (sellIn <= 10) {
                return quality + 2;
            } else {
                return quality + 1;
            }

        } else if ("Conjured".equals(name)) {

            // Degrades twice as fast as normal items
            return quality - (sellIn < 0 ? 4 : 2);

        } else {

            // Normal items and unrecognised item names
            return quality - (sellIn < 0 ? 2 : 1);
        }
    }
}