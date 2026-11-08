// Get the HTML elements
const addItemButton = document.getElementById("add-item-button");
const inventoryBody = document.getElementById("inventory-body");

// New inventory row
function createInventoryRow() {

    // Table row
    const row = document.createElement("tr");
    row.className = "inventory-row";

    // Item name input
    const nameInput = document.createElement("input");
    nameInput.type = "text";
    nameInput.className = "item-name";
    nameInput.placeholder = "e.g. Aged Brie";

    // Sell In input
    const sellInInput = document.createElement("input");
    sellInInput.type = "number";
    sellInInput.className = "item-sell-in";
    sellInInput.value = 0;

    // Quality input
    const qualityInput = document.createElement("input");
    qualityInput.type = "number";
    qualityInput.className = "item-quality";
    qualityInput.value = 0;

    // Remove button
    const removeButton = document.createElement("button");
    removeButton.type = "button";
    removeButton.className = "remove-item-button";
    removeButton.textContent = "Remove";

    // Add each input and button to its own table cell
    const nameCell = document.createElement("td");
    nameCell.appendChild(nameInput);

    const sellInCell = document.createElement("td");
    sellInCell.appendChild(sellInInput);

    const qualityCell = document.createElement("td");
    qualityCell.appendChild(qualityInput);

    const actionCell = document.createElement("td");
    actionCell.appendChild(removeButton);

    // Add all cells to the row
    row.append(nameCell, sellInCell, qualityCell, actionCell);

    return row;
}

// Add a new inventory row when the Add Item button is clicked
addItemButton.addEventListener("click", () => {

    const newRow = createInventoryRow();

    inventoryBody.appendChild(newRow);

    // Automatically focus on the new item's name field
    newRow.querySelector(".item-name").focus();
});

// Handle clicks on Remove buttons
inventoryBody.addEventListener("click", (event) => {

    // Check whether the clicked element is a Remove button
    const removeButton = event.target.closest(".remove-item-button");

    if (!removeButton) {
        return;
    }

    // Find and remove the corresponding inventory row
    const row = removeButton.closest(".inventory-row");

    if (row) {
        row.remove();
    }
});

// Get the elements needed for the API request and results
const updateButton = document.getElementById("update-button");
const statusMessage = document.getElementById("status-message");
const resultSection = document.getElementById("result-section");
const resultsBody = document.getElementById("results-body");

// Display a status message
function setStatus(message, type) {
    statusMessage.textContent = message;
    statusMessage.className = type || "";
}

// Display the updated inventory returned by the backend
function displayResults(items) {

    // Clear any previous results
    resultsBody.replaceChildren();

    // Handle an empty inventory
    if (items.length === 0) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 3;
        cell.textContent = "No inventory items to display.";

        row.appendChild(cell);
        resultsBody.appendChild(row);
    }

    // Create a table row for each updated item
    items.forEach((item) => {

        const row = document.createElement("tr");

        const nameCell = document.createElement("td");
        nameCell.textContent = item.name;

        const sellInCell = document.createElement("td");
        sellInCell.textContent = item.sellIn;

        const qualityCell = document.createElement("td");
        qualityCell.textContent = item.quality;

        row.append(nameCell, sellInCell, qualityCell);

        resultsBody.appendChild(row);
    });

    // Show the results section
    resultSection.hidden = false;
}

// Handle the Update Inventory button
updateButton.addEventListener("click", async () => {

    const items = [];

    // Collect all inventory rows
    const rows = inventoryBody.querySelectorAll(".inventory-row");

    for (const row of rows) {

        const nameInput = row.querySelector(".item-name");
        const sellInInput = row.querySelector(".item-sell-in");
        const qualityInput = row.querySelector(".item-quality");

        const name = nameInput.value.trim();
        const sellInValue = sellInInput.value.trim();
        const qualityValue = qualityInput.value.trim();

        // Validate the item name
        if (!name) {
            setStatus("Please enter an item name.", "error");
            nameInput.focus();
            return;
        }

        // Validate that SellIn and Quality have been entered
        if (sellInValue === "" || qualityValue === "") {
            setStatus("Please enter both Sell In and Quality.", "error");

            if (sellInValue === "") {
                sellInInput.focus();
            } else {
                qualityInput.focus();
            }

            return;
        }

        const sellIn = Number(sellInValue);
        const quality = Number(qualityValue);

        // Ensure both values are integers
        if (!Number.isInteger(sellIn) || !Number.isInteger(quality)) {
            setStatus("Sell In and Quality must be whole numbers.", "error");
            return;
        }

        // Add the item to our request array
        items.push({
            name: name,
            sellIn: sellIn,
            quality: quality
        });
    }

    // Send the inventory to the backend
    try {

        updateButton.disabled = true;
        resultSection.hidden = true;

        setStatus("Updating inventory...", "");

        const response = await fetch("/api/inventory/update", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(items)
        });

        const updatedItems = await response.json();

        // Check whether the backend returned an error
        if (!response.ok) {
            throw new Error(
                updatedItems.error || "Failed to update inventory."
            );
        }

        // Check that the backend returned an array
        if (!Array.isArray(updatedItems)) {
            throw new Error("Unexpected response from the server.");
        }

        // Display the updated inventory
        displayResults(updatedItems);

        setStatus("Inventory successfully updated!", "success");

    } catch (error) {

        setStatus(error.message || "Something went wrong.", "error");

    } finally {

        // Re-enable the button whether the request succeeded or failed
        updateButton.disabled = false;
    }
});
