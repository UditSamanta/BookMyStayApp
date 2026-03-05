import java.util.HashMap;

public class BookMyStayApp {

    // HashMap to store room type and available count
    HashMap<String, Integer> inventory;

    // Constructor to initialize inventory
    RoomInventory() {
        inventory = new HashMap<>();

        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    // Method to get availability
    void getAvailability(String roomType) {
        if (inventory.containsKey(roomType)) {
            System.out.println(roomType + " Available: " + inventory.get(roomType));
        } else {
            System.out.println("Room type not found.");
        }
    }

    // Method to update availability
    void updateAvailability(String roomType, int newCount) {
        if (inventory.containsKey(roomType)) {
            inventory.put(roomType, newCount);
            System.out.println(roomType + " updated to " + newCount);
        } else {
            System.out.println("Room type not found.");
        }
    }

    // Method to display full inventory
    void displayInventory() {
        System.out.println("Current Room Inventory:\n");

        for (String roomType : inventory.keySet()) {
            System.out.println(roomType + " : " + inventory.get(roomType));
        }
    }

    // Main method
    public static void main(String[] args) {

        RoomInventory inv = new RoomInventory();

        // Display initial inventory
        inv.displayInventory();

        System.out.println();

        // Check availability
        inv.getAvailability("Single Room");

        System.out.println();

        // Update availability
        inv.updateAvailability("Single Room", 4);

        System.out.println();

        // Display updated inventory
        inv.displayInventory();
    }
}