package be.howest.nmct.drshopper.Admin.Models;

/**
 * Created by Greg on 17-Nov-15.
 */
public class Ingredient {

    private int id;
    private String name;
    private String quantityName;

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public Boolean isChecked;

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setQuantityName(String quantityName) {
        this.quantityName = quantityName;
    }

    private String quantity;

    public Ingredient(){}

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

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getQuantityName() {
        return quantityName;
    }

    public String getQuantity() {
        return quantity;
    }
}
