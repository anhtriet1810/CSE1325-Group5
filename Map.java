import java.util.List;

public class Map {
    private String name;
    private int width;
    private int height;

    private int MIN_SIZE;
    private int MAX_SIZE;
    private List <Room> rooms;

    public Map () {
        this.name = "Unnamed";
        this.width = 0;
        this.height = 0;
        
        this.MIN_SIZE = 0;
        this.MAX_SIZE = 0;
        this.rooms = null;
    }

    public Map (String name, int width, int height, int MIN, int MAX, List <Room> rooms) {
        setName (name);
        setWidth (width);
        setHeight (height);

        setMinSize (MIN);
        setMaxSize (MAX);
        setRoom (rooms);
    }

    public String getName () {
        return this.name;
    }
    
    public void setName (String name) {
        if (name == null || name.isEmpty()) {
            this.name = "Unnamed";
        }   else {
            this.name = name;
        }

    }

    public int getWidth () {
        return this.width;
    }

    public void setWidth (int width) {
        if (width < 0) {
            this.width = 0;
        }   else {
            this.width = width;
        }
    }
    
    public int getHeight () {
        return this.height;
    }

    public void setHeight (int height) {
        if (width < 0) {
            this.height = 0;
        }   else {
            this.height = height;
        }
    }

    public int getMinSize() {
        return this.MIN_SIZE;
    }

    public void setMinSize (int MINSIZE) {
        if (MINSIZE < 0) {
            this.MIN_SIZE = 0;
        }   else {
            this.MIN_SIZE = MINSIZE;
        }
    }

    public int getMaxSize() {
        return this.MAX_SIZE;
    }

    public void setMaxSize (int MAXSIZE) {
        if (MAXSIZE < 0) {
            this.MAX_SIZE = 0;
        }   else {
            this.MAX_SIZE = MAXSIZE;
        }
    }

    public List<Room> getRooms() {
        return this.rooms;
    }

    public void setRoom(List<Room> rooms) {
        this.rooms = rooms;
    }

}
