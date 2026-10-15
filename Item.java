import java.util.Objects;

public class Item {
    private String name;
    private int quantity;
    private String description;
    private String roomId;

    // Initialization
    public Item() {
        this.name = "Unknown";
        this.quantity = 0;
        this.description = "No description";
        this.roomId = "Unknown";
    }

    public Item(String name, int quantity, String category, String description, String roomId) {
        setName(name);
        setQuantity(quantity);
        setDescription(description);
        setRoomId(roomId);
    }

    // Mutators and Accessors
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.name = "Unknown";
        }   else { 
            this.name = name;
        }
    }
    
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            this.quantity = 0;
        } else {
            this.quantity = quantity;
        }
    }

    public String description() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.isEmpty()) {
            this.description = "No description";
        } else {
            this.description = description;
        }
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }


    // Additional methods 
    public void restock(int amount) {
        this.quantity += amount;
    }

    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                ", roomId='" + roomId + '\'' +
                '}';
    }
}
