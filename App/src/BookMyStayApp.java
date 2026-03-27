import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * USE CASE 4: Room Search & Availability Check
 * Focus: Read-Only access, Separation of Concerns, and Defensive Programming.
 */

// --- DOMAIN LAYER ---

enum RoomType {
    SINGLE, DOUBLE, SUITE, DELUXE
}

/**
 * Room Object (Domain Model)
 * Represents the static details of a room.
 * Use of 'final' and lack of setters ensures Read-Only Access.
 */
class Room {
    private final RoomType type;
    private final double pricePerNight;
    private final String description;

    public Room(RoomType type, double pricePerNight, String description) {
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.description = description;
    }

    public RoomType getType() { return type; }
    public double getPricePerNight() { return pricePerNight; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format("%-10s | %-25s | $%.2f/night", type, description, pricePerNight);
    }
}

// --- DATA ACCESS / STATE LAYER ---

/**
 * Inventory Manager (State Holder)
 * Centralizes the count of available rooms.
 */
class InventoryManager {
    private final Map<RoomType, Integer> roomCounts = new HashMap<>();

    public void setInventory(RoomType type, int count) {
        roomCounts.put(type, count);
    }

    /**
     * Defensive Programming:
     * Returns the current count without allowing the caller to modify the internal Map.
     */
    public int getAvailableCount(RoomType type) {
        return roomCounts.getOrDefault(type, 0);
    }
}

// --- SERVICE LAYER ---

/**
 * Search Service
 * Handles the logic for filtering and displaying rooms based on availability.
 * This service is decoupled from the "Booking/Write" logic.
 */
class RoomSearchService {
    private final InventoryManager inventory;
    private final List<Room> roomCatalog;

    public RoomSearchService(InventoryManager inventory, List<Room> roomCatalog) {
        this.inventory = inventory;
        this.roomCatalog = roomCatalog;
    }

    /**
     * Core Logic: Filters out rooms with 0 availability.
     * Ensures System State remains unchanged (No updates performed here).
     */
    public List<Room> searchAvailableRooms() {
        List<Room> availableResults = new ArrayList<>();

        for (Room room : roomCatalog) {
            // Retrieve availability from centralized inventory
            int availableCount = inventory.getAvailableCount(room.getType());

            // Validation Logic: Only include actionable options (count > 0)
            if (availableCount > 0) {
                availableResults.add(room);
            }
        }
        return availableResults;
    }
}

// --- APPLICATION ENTRY POINT ---

public class BookMyStayApp {
    public static void main(String[] args) {
        // 1. Initialize Centralized Inventory
        InventoryManager inventory = new InventoryManager();
        inventory.setInventory(RoomType.SINGLE, 5);
        inventory.setInventory(RoomType.DOUBLE, 0); // Out of stock - should be filtered
        inventory.setInventory(RoomType.SUITE, 2);
        inventory.setInventory(RoomType.DELUXE, 1);

        // 2. Define Room Catalog (Domain Objects)
        List<Room> catalog = List.of(
                new Room(RoomType.SINGLE, 100.0, "Cozy single bed"),
                new Room(RoomType.DOUBLE, 180.0, "Spacious double bed"),
                new Room(RoomType.SUITE, 350.0, "Luxury suite with balcony"),
                new Room(RoomType.DELUXE, 500.0, "Penthouse deluxe experience")
        );

        // 3. Initialize Search Service (Separation of Concerns)
        RoomSearchService searchService = new RoomSearchService(inventory, catalog);

        // 4. Guest Action: Initiate Search
        System.out.println("=== BOOK MY STAY: AVAILABLE ROOMS ===");
        System.out.println(String.format("%-10s | %-25s | %-12s | %-10s", "TYPE", "DESCRIPTION", "PRICE", "AVAILABLE"));
        System.out.println("--------------------------------------------------------------------------");

        List<Room> availableRooms = searchService.searchAvailableRooms();

        if (availableRooms.isEmpty()) {
            System.out.println("No rooms are currently available.");
        } else {
            for (Room room : availableRooms) {
                int count = inventory.getAvailableCount(room.getType());
                System.out.println(room + " | Qty: " + count);
            }
        }

        System.out.println("--------------------------------------------------------------------------");
        System.out.println("Search Complete. System state remains unchanged.");
    }
}