import java.util.ArrayList;

public class ItemManager {
    private ArrayList<Item> items;

    public ItemManager() {
        items = new ArrayList<Item>();
    }

    public Item createItem(String name, int quantity, String description) {
        Item item = new Item(name, quantity, description);
        return item;
    }

    public void deleteItem(Item item) {
        if (items.contains(item)) {
            items.remove(item);
        } else {
            System.err.println(String.format("Item:%s does not exist in the Item list.", item));
        }
    }

    public void editItem(Item item, String name, int quantity, String description) {
        if (items.contains(item)) {
            int index = items.indexOf(item);
            items.set(index, createItem(name, quantity, description));
        } else
        {
             System.err.println(String.format("Item:%s does not exist in the Item list.", item));
        }
    }

    public ArrayList<Item> getItems()
    {
        return items;
    }
}