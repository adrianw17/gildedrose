# Gilded Rose Inventory Updater

A full-stack application built for the Gilded Rose inn. It uses a Spring Boot REST API to update inventory items and a basic web page to enter and view items.

## Built With

- Java 21
- Spring Boot 4.1.2
- Maven
- HTML, CSS and JavaScript

## Run the Application

```text
./mvnw spring-boot:run
```

When the application starts, open in browser:

```text
http://localhost:8080/
```

## How to Use

1. Enter an item's name, Sell In value and Quality.
2. Select "Add Item" to add another row, or "Remove" to remove one.
3. Select "Update Inventory".
4. The updated inventory will appear below the input table.

## Inventory Rules

The application updates the inventory by one day at a time.

- **Normal items:** Quality decreases by 1 per day, or by 2 once the item is already past its sell-by date.
- **Aged Brie:** Quality increases by 1 per day, or by 2 once it is already past its sell-by date.
- **Backstage passes:** Quality increases by 1 when more than 10 days remain, by 2 when 6–10 days remain, and by 3 when 0–5 days remain. Quality becomes 0 after the concert.
- **Sulfuras:** Does not change. Its Quality remains 80.
- **Conjured items:** Quality decreases by 2 per day, or by 4 once the item is already past its sell-by date.

Quality cannot go below 0 or above 50 for items other than Sulfuras.

## REST API

The frontend sends inventory to this endpoint:

```http
POST /api/inventory/update
Content-Type: application/json
```

Example request:

```json
[
  {"name": "Aged Brie", "sellIn": 1, "quality": 1},
  {"name": "Backstage passes", "sellIn": 9, "quality": 2},
  {"name": "Sulfuras", "sellIn": 0, "quality": 80}
]
```

The API returns the updated inventory as JSON. Invalid item names return `400 Bad Request`.

## Run the Tests

```text
./mvnw test
```

## Notes

- The application processes inventory in memory; it does not use a database.
- Unknown item names are treated as normal items.
