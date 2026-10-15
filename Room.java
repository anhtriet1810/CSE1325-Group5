import java.util.List;
import java.util.ArrayList;

public class Room {
    private int roomNumber;
    private String name;
    private String type;
    private int personCap;
    private int itemCap;
    private int x_location;
    private int y_location;
    private List<Item> items;

    // Intializting default room values
    public Room () {
        this.roomNumber = 0;
        this.name = "Unnamed";
        this.type = "Undefined";
        this.personCap = 0;
        this.itemCap = 0;
        this.x_location = -1;
        this.y_location = -1;
        this.items = null;
    }

    public Room(int roomNumber, String name, String type, int personCap, int itemCap, int x_location, int y_location, List<Item> items) {
        setRoomNumber(roomNumber);
        setName(name);
        setType(type);
        setPersonCap(personCap);
        setItemCap(itemCap);
        setX_location(x_location);
        setY_location(y_location);
        setItems(items);
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        if (roomNumber < 0) {
            this.roomNumber = 0;
        } else {
            this.roomNumber = roomNumber;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.name = "Storage";
        } else {
            this.name = name;
        }
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null || type.isEmpty()) {
            this.type = "Undefined";
        } else {
            this.type = type;
        }
    }


    public int getPersonCap() {
        return personCap;
    }

    public void setPersonCap(int personCap) {
        if (personCap < 0) {
            this.personCap = 0;
        } else {
            this.personCap = personCap;
        }
    }

    public int getItemCap() {
        return itemCap;
    }

    public void setItemCap(int itemCap) {
        if (itemCap < 0) {
            this.itemCap = 0;
        } else {
            this.itemCap = itemCap;
        }
    }

    public int getX_location() {
        return x_location;
    }

    public void setX_location(int x_location) {
        if (x_location < 0) {
            this.x_location = 0;
        } else {
            this.x_location = x_location;
        }
    }

    public int getY_location() {
        return y_location;
    }

    public void setY_location(int y_location) {
        if (y_location < 0) {
            this.y_location = 0;
        } else {
            this.y_location = y_location;
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem (Item item) {
        if (this.items == null) {
            this.items = new ArrayList<>(); 
        }
        if (item == null) {
            System.out.println("Cannot add null item to the room.");
        }
        this.items.add(item);
    }

    public void printItems() {
        for (Item c : this.items){
            System.out.println(c);
        }
    }

    public void removeItem(Item item) {
        if (this.items == null || item == null) {
            System.out.println("Cannot remove null item or from an empty list.");
            return;
        }
        this.items.remove(item);
    }

    public String toString() {
        return "Room{" +
                "roomNumber=" + roomNumber +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", personCap=" + personCap +
                ", itemCap=" + itemCap +
                ", x_location=" + x_location +
                ", y_location=" + y_location +
                ", items=" + items +
                '}';
    }
}   