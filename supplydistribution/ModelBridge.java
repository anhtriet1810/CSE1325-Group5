package supplydistribution;

/**
 * Thin adapters between the live GUI model ({@link RoomView}) and the backend
 * {@link Room} type used in UML / debug helpers. MapGUI continues to own
 * RoomView; these helpers keep both layers conceptually aligned.
 */
public final class ModelBridge {
    private ModelBridge() {}

    /**
     * Converts a GUI room into a backend Room.
     * Backend {@code type} is not stored on RoomView, so {@code "General"} is used.
     */
    public static Room toRoom(RoomView view) {
        Room room = new Room(
                view.getNumber(),
                view.getName(),
                "General",
                view.getPersonCapacity(),
                view.getItemCapacity(),
                view.getX(),
                view.getY());
        for (Item item : view.getItems()) {
            room.addItem(new Item(item.getName(), item.getQuantity(), item.getDescription()));
        }
        return room;
    }

    /**
     * Converts a backend Room into a GUI RoomView (type is dropped; GUI has no type field).
     */
    public static RoomView toRoomView(Room room) {
        RoomView view = new RoomView(
                room.getName(),
                room.getRoomNumber(),
                room.getItemCap(),
                room.getPersonCap(),
                room.getXLocation(),
                room.getYLocation());
        for (Item item : room.getItems()) {
            view.addItem(new Item(item.getName(), item.getQuantity(), item.getDescription()));
        }
        return view;
    }

    /**
     * Same conceptual move as MapGUI: transfer qty of an item from source to destination
     * via {@link RoomManager}, using case-insensitive name matching on the backend Room.
     */
    public static void moveItem(RoomManager manager, Item item, int qty, Room source, Room destination) {
        manager.moveItem(item, qty, source, destination);
    }
}
