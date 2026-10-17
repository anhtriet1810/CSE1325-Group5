import java.util.ArrayList;
import java.util.List;

public class Map {
    private String name;
    private int width;
    private int height;
    private final int MIN_SIZE = 10;
    private final int MAX_SIZE = 1000;
    private List<Room> rooms;

    public Map(String name, int w, int h) {
        if (w < MIN_SIZE || w > MAX_SIZE || h < MIN_SIZE || h > MAX_SIZE) {
            throw new IllegalArgumentException("Map dimensions must be between " + MIN_SIZE + " and " + MAX_SIZE);
        }
        this.name = name;
        this.width = w;
        this.height = h;
        this.rooms = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Room> getRoomList() {
        return rooms;
    }

    public Room[] getRooms() {
        return rooms.toArray(new Room[0]);
    }

    @Override
    public String toString() {
        return "Map: " + name + " [" + width + "x" + height + "] Rooms: " + rooms.size();
    }
}