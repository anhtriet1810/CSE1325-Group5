package supplydistribution;
import java.util.ArrayList;

/// backend file

public class ItemManager {
    private ArrayList<Item> items;

    //constructor
    public ItemManager() {
        items = new ArrayList<Item>();
    }

    //adds an Item to the Item arrayList.
    public void addItem(Item item)
    {
        //if duplicate, simply add a quantity to that item
        boolean duplicate = false;
        for(Item i : items)
        {
            if(item.equals(i))
            {
                editItem(i, i.getName(), i.getQuantity(), i.getDescription()); //you left off here
                duplicate = true;
            }
        }

        if(!duplicate)
        {
            items.add(item);
        }
    }

    //creates a new Item object, does not add it to the Item arrayList.
    public Item createItem(String name, int quantity, String description) {
        Item item = new Item(name, quantity, description);
        return item;
    }

    //removes an Item from the Item arrayList.
    public void deleteItem(Item item) {
        if (items.contains(item)) {
            items.remove(item);
        } else {
            System.err.println(String.format("Item:%s does not exist in the Item list.", item));
        }
    }

    //allows you to change the Item specified (if found) into another item.
    public void editItem(Item item, String name, int quantity, String description) {
        if (items.contains(item)) {
            int index = items.indexOf(item);
            items.set(index, createItem(name, quantity, description));
        } else
        {
             System.err.println(String.format("Item:%s does not exist in the Item list.", item));
        }
    }
    public void editItem(Item item, Item newItem) {
        if (items.contains(item)) {
            int index = items.indexOf(item);
            items.set(index, newItem);
        } else
        {
             System.err.println(String.format("Item:%s does not exist in the Item list.", item));
        }
    }

    //returns the Item arrayList
    public ArrayList<Item> getItems()
    {
        return items;
    }
}