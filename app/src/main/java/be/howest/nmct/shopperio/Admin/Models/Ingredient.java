package be.howest.nmct.shopperio.Admin.Models;

/**
 * Created by Greg on 17-Nov-15.
 */
public class Ingredient {

    public Boolean isChecked;
    private int id;
    private String name;
    private String quantityName;
    private String quantity;

    public Ingredient() {
    }

    public Ingredient(String name) {
        this.name = name;
    }

    public Ingredient(int id, String name, String quantityName, String quantity) {
        this.id = id;
        this.name = name;
        this.quantityName = quantityName;
        this.quantity = quantity;
    }

    public Ingredient(String name, String quantityName, String quantity) {
        this.id = id;
        this.name = name;
        this.quantityName = quantityName;
        this.quantity = quantity;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getQuantityName() {
        return quantityName;
    }

    public void setQuantityName(String quantityName) {
        this.quantityName = quantityName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
