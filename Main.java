import supplydistribution.*;

/**
 * Application entry. Default path launches the Swing UI.
 * Pass {@code --demo} for a short backend console smoke check (RoomManager / ItemManager).
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && "--demo".equals(args[0])) {
            runBackendDemo();
            return;
        }
        UserInterface.UserInterfaceAppears();
    }

    /** Optional console demo of backend helpers; not used by the GUI. */
    private static void runBackendDemo() {
        Map campusMap = new Map("Main Campus", 100, 100);
        RoomManager roomMgr = new RoomManager(campusMap);
        ItemManager itemMgr = new ItemManager();

        Room lab = new Room(101, "Hardware Lab", "Lab", 25, 20, 10, 20);
        Room storage = new Room(102, "Storage Closet", "Storage", 2, 50, 12, 20);
        roomMgr.addRoom(lab);
        roomMgr.addRoom(storage);

        Item chairs = itemMgr.createItem("Rolling Chair", 15, "Office chairs");
        lab.addItem(chairs);

        System.out.println("Lab items before transfer: " + lab.getCurrentItemCount());
        roomMgr.moveItem(chairs, 5, lab, storage);
        System.out.println("Lab items after transfer: " + lab.getCurrentItemCount());
        System.out.println("Storage items after transfer: " + storage.getCurrentItemCount());
    }
}
