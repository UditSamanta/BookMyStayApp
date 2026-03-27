import java.util.*;

/**
 * BOOK MY STAY SYSTEM
 * Use Case 4: Room Search & Availability Check
 * Use Case 5: Booking Request (FIFO Queue)
 */

// 1. SHARED DOMAIN MODELS
enum RoomType {
    SINGLE, DOUBLE, SUITE, DELUXE
}

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

/**
 * Use Case 5: Reservation intent object
 */
class Reservation {
    private final String requestId;
    private final String guestName;
    private final RoomType requestedType;

    public Reservation(String guestName, RoomType requestedType) {
        this.requestId = UUID.randomUUID().toString().substring(0, 8);
        this.guestName = guestName;
        this.requestedType = requestedType;
    }

    @Override
    public String toString() {
        return "ID: " + requestId + " | Guest: " + guestName + " | Room: " + requestedType;
    }
}

// 2. SERVICES

class InventoryManager {
    private final Map<RoomType, Integer> roomCounts = new HashMap<>();

    public void setInventory(RoomType type, int count) {
        roomCounts.put(type, count);
    }

    public int getAvailableCount(RoomType type) {
        return roomCounts.getOrDefault(type, 0);
    }
}

class RoomSearchService {
    private final InventoryManager inventory;
    private final List<Room> roomCatalog;

    public RoomSearchService(InventoryManager inventory, List<Room> roomCatalog) {
        this.inventory = inventory;
        this.roomCatalog = roomCatalog;
    }

    public List<Room> searchAvailableRooms() {
        List<Room> results = new ArrayList<>();
        for (Room room : roomCatalog) {
            if (inventory.getAvailableCount(room.getType()) > 0) {
                results.add(room);
            }
        }
        return results;
    }
}

class BookingRequestQueue {
    private final Queue<Reservation> requestQueue = new LinkedList<>();

    public void submitRequest(Reservation res) {
        requestQueue.add(res);
        System.out.println("[Queue] Request added for: " + res);
    }

    public void processAll() {
        System.out.println("\n--- Processing FIFO Queue ---");
        while (!requestQueue.isEmpty()) {
            System.out.println("Processing: " + requestQueue.poll());
        }
    }
}

// 3. MAIN EXECUTION
public class BookMyStayApp {
    public static void main(String[] args) {
        // Setup
        InventoryManager inventory = new InventoryManager();
        inventory.setInventory(RoomType.SINGLE, 2);
        inventory.setInventory(RoomType.SUITE, 1);

        List<Room> catalog = List.of(
                new Room(RoomType.SINGLE, 100.0, "Cozy Single"),
                new Room(RoomType.SUITE, 300.0, "Luxury Suite")
        );

        // UC4: Search
        RoomSearchService searchService = new RoomSearchService(inventory, catalog);
        System.out.println("Available Rooms:");
        searchService.searchAvailableRooms().forEach(System.out::println);
        System.out.println();

        // UC5: Booking Intake (Fairness)
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.submitRequest(new Reservation("Alice", RoomType.SUITE));
        queue.submitRequest(new Reservation("Bob", RoomType.SINGLE));

        // Final process
        queue.processAll();
    }
}