package supplydistribution;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// frontend file

/**
 * Room Supply Distribution System - RoomView
 */
public class RoomView {
    // Map layout limits (the map has a fixed maximum size).
    public static final int WIDTH = 150;
    public static final int HEIGHT = 100;
    public static final int MAP_WIDTH = 720;
    public static final int MAP_HEIGHT = 500;
    public static final int MAX_ROOMS = 16;

    // Value limits.
    public static final int MAX_NAME = 40;
    public static final int MAX_NUMBER = 99_999;
    public static final int MAX_CAPACITY = 1_000_000;

    private String name;
    private int number;
    private int itemCapacity;
    private int personCapacity;
    private int x;
    private int y;
    private final List<Item> items = new ArrayList<>();

    public RoomView(String name, int number, int itemCapacity, int personCapacity, int x, int y) {
        update(name, number, itemCapacity, personCapacity);
        moveTo(x, y);
    }

    /** Position of the n-th default slot (4 columns x 4 rows), used for auto-placement. */
    public static Point gridSlot(int index) {
        return new Point(15 + (index % 4) * 175, 15 + (index / 4) * 120);
    }

    /** Changes the room's details after validating them. Nothing changes if it throws. */
    public void update(String newName, int newNumber, int newItemCapacity, int newPersonCapacity) {
        String cleanName = newName == null ? "" : newName.trim();
        if (cleanName.isEmpty()) {
            throw new IllegalArgumentException("Room name cannot be empty.");
        }
        if (cleanName.length() > MAX_NAME) {
            throw new IllegalArgumentException("Room name must be at most " + MAX_NAME + " characters.");
        }
        if (newNumber < 0 || newNumber > MAX_NUMBER) {
            throw new IllegalArgumentException("Room number must be between 0 and " + MAX_NUMBER + ".");
        }
        if (newItemCapacity < 0 || newItemCapacity > MAX_CAPACITY) {
            throw new IllegalArgumentException("Item capacity must be between 0 and " + MAX_CAPACITY + ".");
        }
        if (newPersonCapacity < 0 || newPersonCapacity > MAX_CAPACITY) {
            throw new IllegalArgumentException("Person capacity must be between 0 and " + MAX_CAPACITY + ".");
        }
        if (newItemCapacity < totalQuantity()) {
            throw new IllegalArgumentException("Item capacity cannot be less than the "
                    + totalQuantity() + " units already stored in this room.");
        }
        name = cleanName;
        number = newNumber;
        itemCapacity = newItemCapacity;
        personCapacity = newPersonCapacity;
    }

    /** Moves the room, keeping it inside the map. */
    public void moveTo(int newX, int newY) {
        x = Math.max(0, Math.min(newX, MAP_WIDTH - WIDTH));
        y = Math.max(0, Math.min(newY, MAP_HEIGHT - HEIGHT));
    }

    // ---- items --------------------------------------------------------------

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int totalQuantity() {
        int total = 0;
        for (Item item : items) total += item.getQuantity();
        return total;
    }

    public int remainingCapacity() {
        return itemCapacity - totalQuantity();
    }

    /** Index of the item with this name (ignoring case), or -1. */
    public int indexOf(String itemName) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equalsIgnoreCase(itemName)) return i;
        }
        return -1;
    }

    /** Adds an item; if the room already has one with that name, the quantities are combined. */
    public void addItem(Item item) {
        if (item.getQuantity() > remainingCapacity()) {
            throw new IllegalArgumentException("Not enough capacity in room " + number
                    + " (" + remainingCapacity() + " units free, " + item.getQuantity() + " needed).");
        }
        int existing = indexOf(item.getName());
        if (existing >= 0) {
            Item old = items.get(existing);
            items.set(existing, old.withQuantity(old.getQuantity() + item.getQuantity()));
        } else {
            items.add(item);
        }
    }

    /** Replaces the item at index (used by Edit Item). */
    public void replaceItem(int index, Item updated) {
        Item old = items.get(index);
        int other = indexOf(updated.getName());
        if (other >= 0 && other != index) {
            throw new IllegalArgumentException("This room already has an item named \"" + updated.getName() + "\".");
        }
        if (totalQuantity() - old.getQuantity() + updated.getQuantity() > itemCapacity) {
            throw new IllegalArgumentException("That quantity exceeds this room's item capacity.");
        }
        items.set(index, updated);
    }

    public void removeItem(int index) {
        items.remove(index);
    }

    /** Removes "amount" units of the item at index; the item disappears if none are left. */
    public void reduceItem(int index, int amount) {
        Item item = items.get(index);
        if (amount < 1 || amount > item.getQuantity()) {
            throw new IllegalArgumentException("Amount must be between 1 and " + item.getQuantity() + ".");
        }
        if (amount == item.getQuantity()) {
            items.remove(index);
        } else {
            items.set(index, item.withQuantity(item.getQuantity() - amount));
        }
    }

    // ---- getters ------------------------------------------------------------

    public String getName() { return name; }
    public int getNumber() { return number; }
    public int getItemCapacity() { return itemCapacity; }
    public int getPersonCapacity() { return personCapacity; }
    public int getX() { return x; }
    public int getY() { return y; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    @Override
    public String toString() {
        return number + " - " + name;
    }
}
