import java.util.*;

/**
 * BOOK MY STAY - INTEGRATED SYSTEM
 * --------------------------------
 * UC4: Search & Availability Check
 * UC5: Booking Request (FIFO Queue)
 * UC6: Reservation Confirmation & Allocation (Set-based uniqueness)
 */

// --- 1. DOMAIN MODELS ---

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

class Reservation {
    private final String requestId;
    private final String guestName;
    private final RoomType requestedType;

    public Reservation(String guestName, RoomType requestedType) {
        this.requestId = UUID.randomUUID().toString().substring(0, 8);
        this.guestName = guestName;
        this.requestedType = requestedType;
    }

    public String getGuestName() { return guestName; }
    public RoomType getRequestedType() { return requestedType; }

    @Override
    public String toString() {
        return "Request [#" + requestId + "] for " + guestName + " (" + requestedType + ")";
    }
}

// --- 2. STATE & DATA MANAGEMENT ---

class InventoryManager {
    private final Map<RoomType, Integer> roomCounts = new HashMap<>();

    public void updateInventory(RoomType type, int count) {
        roomCounts.put(type, count);
    }

    public int getAvailableCount(RoomType type) {
        return roomCounts.getOrDefault(type, 0);
    }
}

// --- 3. CORE SERVICES ---

/**
 * Use Case 5: Manages the arrival order (Fairness)
 */
class BookingRequestQueue {
    private final Queue<Reservation> queue = new LinkedList<>();

    public void submitRequest(Reservation res) {
        queue.add(res);
        System.out.println("System: Received request from " + res.getGuestName());
    }

    public Reservation getNextRequest() { return queue.poll(); }
    public boolean hasPendingRequests() { return !queue.isEmpty(); }
}

/**
 * Use Case 6: Handles uniqueness and inventory deduction
 */
class BookingService {
    private final InventoryManager inventory;
    private final BookingRequestQueue intakeQueue;

    // Set ensures no Room ID is ever double-booked
    private final Map<RoomType, Set<String>> allocatedRoomIds = new HashMap<>();

    public BookingService(InventoryManager inventory, BookingRequestQueue intakeQueue) {
        this.inventory = inventory;
        this.intakeQueue = intakeQueue;
        for (RoomType type : RoomType.values()) {
            allocatedRoomIds.put(type, new HashSet<>());
        }
    }

    public void processBookings() {
        System.out.println("\n--- Processing Booking Allocations ---");
        while (intakeQueue.hasPendingRequests()) {
            Reservation request = intakeQueue.getNextRequest();
            RoomType type = request.getRequestedType();
            int currentQty = inventory.getAvailableCount(type);

            if (currentQty > 0) {
                // Generate a unique ID (e.g., SUITE-1)
                String newRoomId = type.name() + "-" + (allocatedRoomIds.get(type).size() + 1);

                // Add to Set (Uniqueness Enforcement)
                allocatedRoomIds.get(type).add(newRoomId);

                // Immediate Inventory Synchronization
                inventory.updateInventory(type, currentQty - 1);

                System.out.println("SUCCESS: " + request.getGuestName() + " assigned Room " + newRoomId);
            } else {
                System.out.println("FAILED: No availability for " + request.getGuestName() + " (" + type + ")");
            }
        }
        System.out.println("--- End of Processing ---\n");
    }
}

// --- 4. MAIN APPLICATION ---

public class BookMyStayApp {
    public static void main(String[] args) {
        // Initialization
        InventoryManager inventory = new InventoryManager();
        inventory.updateInventory(RoomType.SUITE, 1);  // Limited supply
        inventory.updateInventory(RoomType.SINGLE, 5);

        BookingRequestQueue intakeQueue = new BookingRequestQueue();
        BookingService bookingService = new BookingService(inventory, intakeQueue);

        // UC5: First-Come-First-Served intake
        System.out.println("--- Guest Intake ---");
        intakeQueue.submitRequest(new Reservation("Alice", RoomType.SUITE));
        intakeQueue.submitRequest(new Reservation("Bob", RoomType.SUITE)); // Will be rejected (FIFO)
        intakeQueue.submitRequest(new Reservation("Charlie", RoomType.SINGLE));

        // UC6: Process Queue
        bookingService.processBookings();

        // Check Final State
        System.out.println("Final Suite Count: " + inventory.getAvailableCount(RoomType.SUITE));
    }
}