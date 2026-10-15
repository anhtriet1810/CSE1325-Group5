import java.util.Objects;

public class Item {

    private String name;
    private int quantity;
    private String description;


    public Item (String name, int quantity, String description) {
        setName(name);
        setQuantity(quantity);
        setDescription(description);
    }

    // Accessor methods & Mutator methods for Item class fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.name = "Unnamed";
        }   else {
            this.name = name;
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            this.quantity = 0;
        } else {
            this.quantity = quantity;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.isEmpty()) {
            this.description = "No description";
        } else {
            this.description = description;
        }
    }
    @Override 
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                '}';
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name) &&
                Objects.equals(description, item.description);
    }


}