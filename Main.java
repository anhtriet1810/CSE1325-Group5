public class Main {
    public static void main(String[] args) {
        Map campusMap = new Map("Main Campus", 100, 100);
        RoomManager roomMgr = new RoomManager(campusMap);
        ItemManager itemMgr = new ItemManager();

        // 1. Create and add rooms
        Room lab = new Room(101, "Hardware Lab", "Lab", 25, 20, 10, 20);
        Room storage = new Room(102, "Storage Closet", "Storage", 2, 50, 12, 20);
        roomMgr.addRoom(lab);
        roomMgr.addRoom(storage);

        // 2. Create catalog items and add to lab
        Item chairs = itemMgr.createItem("Rolling Chair", 15, "Office chairs");
        lab.addItem(chairs);

        System.out.println("Lab items before transfer: " + lab.getCurrentItemCount());

        // 3. Move 5 chairs from Lab to Storage
        roomMgr.moveItem(chairs, 5, lab, storage);

        System.out.println("Lab items after transfer: " + lab.getCurrentItemCount());
        System.out.println("Storage items after transfer: " + storage.getCurrentItemCount());
    }
}