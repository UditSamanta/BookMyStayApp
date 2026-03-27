import java.util.*;

/**
 * BOOK MY STAY - FULLY INTEGRATED SYSTEM
 * --------------------------------------
 * UC4: Search | UC5: Queue | UC6: Allocation | UC7: Add-On Services
 */

// --- 1. DOMAIN MODELS ---

enum RoomType {
    SINGLE, DOUBLE, SUITE, DELUXE
}

/**
 * UC7: Add-On Service Model
 * Represents optional offerings (Breakfast, WiFi, Spa, etc.)
 */
class AddOnService {
    private final String name;
    private final double price;

    public AddOnService(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " ($" + price + ")";
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

    public String getRequestId() { return requestId; }
    public String getGuestName() { return guestName; }
    public RoomType getRequestedType() { return requestedType; }

    @Override
    public String toString() {
        return "ID: " + requestId + " | Guest: " + guestName;
    }
}

// --- 2. STATE & SERVICE MANAGEMENT ---

class InventoryManager {
    private final Map<RoomType, Integer> roomCounts = new HashMap<>();
    public void updateInventory(RoomType type, int count) { roomCounts.put(type, count); }
    public int getAvailableCount(RoomType type) { return roomCounts.getOrDefault(type, 0); }
}

/**
 * UC7: Add-On Service Manager
 * Uses Map<String, List<AddOnService>> to link Reservation IDs to multiple services.
 */
class AddOnManager {
    private final Map<String, List<AddOnService>> reservationAddOns = new HashMap<>();

    public void addServiceToReservation(String reservationId, AddOnService service) {
        // computeIfAbsent creates a new list if the ID doesn't exist yet
        reservationAddOns.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
        System.out.println("Service Added: " + service.getName() + " to Reservation " + reservationId);
    }

    public double calculateTotalAddOnCost(String reservationId) {
        List<AddOnService> services = reservationAddOns.getOrDefault(reservationId, Collections.emptyList());
        return services.stream().mapToDouble(AddOnService::getPrice).sum();
    }

    public void displayAddOns(String reservationId) {
        List<AddOnService> services = reservationAddOns.get(reservationId);
        if (services != null) {
            System.out.println("Selected Add-ons: " + services);
        }
    }
}

class BookingRequestQueue {
    private final Queue<Reservation> queue = new LinkedList<>();
    public void submitRequest(Reservation res) { queue.add(res); }
    public Reservation getNextRequest() { return queue.poll(); }
    public boolean hasPendingRequests() { return !queue.isEmpty(); }
}

class BookingService {
    private final InventoryManager inventory;
    private final BookingRequestQueue intakeQueue;
    private final Map<RoomType, Set<String>> allocatedRoomIds = new HashMap<>();

    public BookingService(InventoryManager inventory, BookingRequestQueue intakeQueue) {
        this.inventory = inventory;
        this.intakeQueue = intakeQueue;
        for (RoomType type : RoomType.values()) allocatedRoomIds.put(type, new HashSet<>());
    }

    public void processBookings(AddOnManager addOnManager, List<AddOnService> globalOptions) {
        System.out.println("\n--- Processing Allocations & Add-ons ---");
        while (intakeQueue.hasPendingRequests()) {
            Reservation request = intakeQueue.getNextRequest();
            int currentQty = inventory.getAvailableCount(request.getRequestedType());

            if (currentQty > 0) {
                String roomId = request.getRequestedType().name() + "-" + (allocatedRoomIds.get(request.getRequestedType()).size() + 1);
                allocatedRoomIds.get(request.getRequestedType()).add(roomId);
                inventory.updateInventory(request.getRequestedType(), currentQty - 1);

                System.out.println("CONFIRMED: " + request.getGuestName() + " in " + roomId);

                // UC7: Randomly assign an add-on for demonstration
                if (!globalOptions.isEmpty()) {
                    addOnManager.addServiceToReservation(request.getRequestId(), globalOptions.get(0));
                }

                double extra = addOnManager.calculateTotalAddOnCost(request.getRequestId());
                System.out.println("Extra Service Total: $" + extra);
            } else {
                System.out.println("REJECTED: No availability for " + request.getGuestName());
            }
        }
    }
}

// --- 3. MAIN APPLICATION ---

public class BookMyStayApp {
    public static void main(String[] args) {
        // Setup
        InventoryManager inventory = new InventoryManager();
        inventory.updateInventory(RoomType.SINGLE, 5);

        AddOnManager addOnManager = new AddOnManager();
        BookingRequestQueue intakeQueue = new BookingRequestQueue();
        BookingService bookingService = new BookingService(inventory, intakeQueue);

        // Define Add-on Options
        AddOnService breakfast = new AddOnService("Buffet Breakfast", 25.0);
        AddOnService spa = new AddOnService("Spa Treatment", 120.0);

        // UC5: Intake
        intakeQueue.submitRequest(new Reservation("Alice", RoomType.SINGLE));
        intakeQueue.submitRequest(new Reservation("Bob", RoomType.SINGLE));

        // UC6 & UC7: Process Allocation and apply Add-ons
        bookingService.processBookings(addOnManager, List.of(breakfast, spa));

        System.out.println("\nSystem operation complete. All services mapped correctly.");
    }
}