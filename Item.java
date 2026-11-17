package supplydistribution;

/// backend file

public class Item {
    //fields
    public static final int MAX_NAME = 60;
    public static final int MAX_DESCRIPTION = 200;
    public static final int MAX_QUANTITY = 1_000_000;

    //instance variables
    private String name;
    private int quantity;
    private String description;

    //constructor
    public Item (String name, int quantity, String description) {
        setName(name);
        setQuantity(quantity);
        setDescription(description);
    }

    //get and set methods for name
    public String getName() {
        return name;
    }
    //sets the name of the item, defaults to "Unnamed" if the given name is empty, null, or too long.
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            System.err.println(String.format("Name cannot be empty or null.", MAX_NAME));
            this.name = "Unnamed";
        }
        else if (name.length() > MAX_NAME){
            System.err.println(String.format("Name is too long, must be less than or equal to %s characters", MAX_NAME));
            this.name = "Unnamed";
        }
        else {
            this.name = name;
        }
    }
    
    //get and set methods for quantity
    public int getQuantity() {
        return quantity;
    }
    //sets the quantity of the item, defaults to 0 if the value is too low, or MAX_QUANTITY if the value is too high
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            System.err.println("Quantity must be at least 0. Setting to 0...");
            this.quantity = 0;
        } else if (quantity > MAX_QUANTITY) {
            System.err.println(String.format("Quantity must be at most %s. Setting to %s...", MAX_QUANTITY, MAX_QUANTITY));
            this.quantity = MAX_QUANTITY;
        }
        else {
            this.quantity = quantity;
        }
    }

    //get and set methods for description
    public String getDescription() {
        return description;
    }
    //sets the description of the item, defaults to "No description" if null, empty, or too long
    public void setDescription(String description) {
        if (description == null || description.isEmpty()) {
            this.description = "No description";
        } else if(description.length() > MAX_DESCRIPTION) {
            System.err.println(String.format("Description cannot be greater than %s characters.", MAX_DESCRIPTION));
            this.description = "No description";
        }
        else {
            this.description = description;
        }
    }

    //helper methods

    /** Returns a copy of this item with a different quantity. */
    public Item withQuantity(int newQuantity)
    {
        return new Item(name, newQuantity, description);
    }

    //toString() and equals()
    @Override 
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                '}';
    }
    public boolean equals(Item o) {
        if (this == o) return true;
        if (o == null) return false;
        return name.equals(o.getName()) && quantity == o.getQuantity() && description.equals(o.getDescription());
    }


}