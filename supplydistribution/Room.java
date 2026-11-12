package supplydistribution;
import java.util.ArrayList;

public class Room {
    private int roomNumber;
    private String name;
    private String type;
    private int personCap;
    private int itemCap;
    private int x_location;
    private int y_location;
    private ItemManager itemManager;

    public Room(int num, String name, String type, int personCap, int itemCap, int x_location, int y_location) {
        if (personCap < 0 || itemCap < 0) {
            throw new IllegalArgumentException("Capacities cannot be negative.");
        }
        this.roomNumber = num;
        this.name = name;
        this.type = type;
        this.personCap = personCap;
        this.itemCap = itemCap;
        this.x_location = x_location;
        this.y_location = y_location;
        this.itemManager = new ItemManager();
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getPersonCap() {
        return personCap;
    }

    public int getItemCap() {
        return itemCap;
    }

    public int getXLocation() {
        return x_location;
    }

    public int getYLocation() {
        return y_location;
    }

    public ArrayList<Item> getItems()
    {
        return itemManager.getItems();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPersonCap(int personCap) {
        this.personCap = personCap;
    }

    public void setItemCap(int itemCap) {
        this.itemCap = itemCap;
    }

    public int getCurrentItemCount() {
        int total = 0;
        ArrayList<Item> items = getItems();
        for (Item item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    public void addItem(Item item) {
        if (getCurrentItemCount() + item.getQuantity() > itemCap) {
            throw new IllegalStateException("Room item capacity exceeded.");
        }
        // Merge quantity if item already exists
        ArrayList<Item> items = getItems();
        for (Item existing : items) {
            if (existing.getName().equalsIgnoreCase(item.getName())) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    public void removeItem(Item i) {
        ArrayList<Item> items = getItems();
        items.remove(i);
    }

    public Item removeItem(String itemName, int qty) {
        ArrayList<Item> items = getItems();
        for (Item existing : items) {
            if (existing.getName().equals(itemName)) {
                if (qty <= 0) {
                    throw new IllegalArgumentException("Quantity must be positive.");
                }
                if (qty >= existing.getQuantity()) {
                    items.remove(existing);
                    return existing;
                } else {
                    existing.setQuantity(existing.getQuantity() - qty);
                    return new Item(existing.getName(), qty, existing.getDescription());
                }
            }
        }
        throw new IllegalArgumentException("Item not found in room: " + itemName);
    }

    @Override
    public String toString() {
        return "Room #" + roomNumber + " (" + name + ", Type: " + type + ") at (" + x_location + "," + y_location + ")";
    }
}