public class RoomManager {
    private Map map;

    public RoomManager(Map m) {
        this.map = m;
    }

    public void addRoom(Room r) {
        if (r.getXLocation() < 0 || r.getXLocation() > map.getWidth() ||
            r.getYLocation() < 0 || r.getYLocation() > map.getHeight()) {
            throw new IllegalArgumentException("Room position is outside map boundaries.");
        }
        for (Room existing : map.getRoomList()) {
            if (existing.getRoomNumber() == r.getRoomNumber()) {
                throw new IllegalArgumentException("Room number " + r.getRoomNumber() + " already exists.");
            }
        }
        map.getRoomList().add(r);
    }

    public void removeRoom(Room r) {
        map.getRoomList().remove(r);
    }

    public void editRoom(Room r, String name, String type, int personCap, int itemCap) {
        if (itemCap < r.getCurrentItemCount()) {
            throw new IllegalArgumentException("New item capacity cannot be smaller than current item count.");
        }
        r.setName(name);
        r.setType(type);
        r.setPersonCap(personCap);
        r.setItemCap(itemCap);
    }

    public void moveItem(Item item, int qty, Room a, Room b) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (b.getCurrentItemCount() + qty > b.getItemCap()) {
            throw new IllegalStateException("Destination room capacity exceeded.");
        }

        Item transferredPortion = a.removeItem(item.getName(), qty);
        b.addItem(transferredPortion);
    }
}