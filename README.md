# Book My Stay 🏨

**Book My Stay** is a robust, Core Java-based hotel booking management system designed to demonstrate the practical application of data structures and advanced software engineering principles. 

---

## 🚀 Features & Engineering Milestones

### 🛡️ Core Reliability
* **Thread Safety:** Handled concurrent booking requests using synchronized blocks to prevent race conditions.
* **Error Handling:** Implemented a "Fail-Fast" validation layer with custom exceptions.
* **LIFO Rollback:** Utilized a **Stack** data structure to manage booking cancellations.

### 📊 Advanced Data Management
* **FIFO Request Handling:** Used a **Queue** to ensure guest requests are processed fairly.
* **Uniqueness Enforcement:** Leveraged **Sets** to guarantee that every assigned Room ID is unique.
* **Persistent Storage:** Implemented file-based **Serialization** for system recovery.

---

## 🛠️ Data Structures Used

| Structure | Purpose | Benefit |
| :--- | :--- | :--- |
| **HashMap** | Inventory Mapping | O(1) average time complexity for lookups. |
| **LinkedList** | Booking Queue | Ensures FIFO fairness for guest requests. |
| **HashSet** | Room ID Tracking | Prevents double-booking via uniqueness. |
| **Stack** | Rollback Logic | LIFO logic for undoing recent allocations. |

---

## 📂 Project Structure

```text
src/
└── com.bookmystay/
    ├── models/
    │   ├── Room.java           
    │   ├── Reservation.java    
    │   └── RoomType.java       
    ├── services/
    │   ├── BookingService.java      
    │   ├── InventoryManager.java    
    │   └── PersistenceService.java  
    └── BookMyStayApp.java
