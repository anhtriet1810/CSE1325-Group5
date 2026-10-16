import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoomManager {
    private final Map map;

    public RoomManager(Map map) {
        this.map = Objects.requireNonNull(map, "map cannot be null");
    }

    public void addRoom(Room room) {
        List<Room> rooms = map.getRooms();
        if (rooms == null) {
            rooms = new ArrayList<>();
            map.setRoom(rooms);
        }
        rooms.add(room);
    }

    public boolean removeRoom(Room room) {
        List<Room> rooms = map.getRooms();
        return rooms != null && rooms.remove(room);
    }

    public void editRoom(Room room, String name, String type, int personCap, int itemCap) {
        Objects.requireNonNull(room, "room cannot be null");
        room.setName(name);
        room.setType(type);
        room.setPersonCap(personCap);
        room.setItemCap(itemCap);
    }

    public void moveItem(Item item, int qty, Room a, Room b) {
        Objects.requireNonNull(item, "item cannot be null");
        Objects.requireNonNull(a, "source room cannot be null");
        Objects.requireNonNull(b, "destination room cannot be null");
        if (a == b) {
            throw new IllegalArgumentException("source and destination rooms must differ");
        }
        if (qty <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }

        List<Item> sourceItems = a.getItems();
        Item sourceItem = findItem(sourceItems, item);
        if (sourceItem == null || sourceItem.getQuantity() < qty) {
            throw new IllegalArgumentException("source room does not contain enough of this item");
        }

        List<Item> destinationItems = b.getItems();
        Item destinationItem = findItem(destinationItems, sourceItem);
        if (sourceItem == destinationItem) {
            throw new IllegalStateException("source and destination rooms share the same item instance");
        }
        long destinationQuantity = 0;
        if (destinationItems != null) {
            for (Item destinationEntry : destinationItems) {
                if (destinationEntry != null) {
                    destinationQuantity += destinationEntry.getQuantity();
                }
            }
        }
        if (destinationQuantity + qty > b.getItemCap()) {
            throw new IllegalStateException("destination room does not have enough item capacity");
        }

        sourceItem.setQuantity(sourceItem.getQuantity() - qty);
        if (sourceItem.getQuantity() == 0) {
            sourceItems.remove(sourceItem);
        }

        if (destinationItem == null) {
            if (destinationItems == null) {
                destinationItems = new ArrayList<>();
                b.setItems(destinationItems);
            }
            destinationItems.add(new Item(sourceItem.getName(), qty, sourceItem.getDescription()));
        } else {
            destinationItem.setQuantity(destinationItem.getQuantity() + qty);
        }
    }

    private Item findItem(List<Item> items, Item item) {
        if (items != null) {
            for (Item candidate : items) {
                if (item.equals(candidate)) {
                    return candidate;
                }
            }
        }
        return null;
    }
}
