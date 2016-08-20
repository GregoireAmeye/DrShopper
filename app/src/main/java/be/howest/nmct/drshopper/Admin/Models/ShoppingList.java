package be.howest.nmct.drshopper.Admin.Models;

import java.io.Serializable;
import java.util.List;

public class ShoppingList implements Serializable {
//
    private int id;
    private String naam;
    private String contacten;
    private List<Ingredient> ingredients;

    public void setId(int id) {
        this.id = id;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public ShoppingList(){ }

    public ShoppingList(int id,String naam, String contacten, List<Ingredient> listIngredients, String url) {
        this.id = id;
        this.naam = naam;
        this.contacten = contacten;
        this.ingredients = listIngredients;
        if(url != "" && url != null && url != "null") this.url = url;
        else this.url = "http://legacyproject.human.cornell.edu/files/2011/08/list.2.jpg";
    }

    public void setQuantityForIngredient(Quantity q, int pos){
        String quantityName = q.getQuantityName();
        String quantityValue = q.getQuantityValue();

        if(quantityName!=null&&quantityName!="null")
            ingredients.get(pos).setQuantityName(quantityName);
        else
            ingredients.get(pos).setQuantityName("");

        if(quantityValue!=null&&quantityValue!="null")
            ingredients.get(pos).setQuantity(quantityValue);
        else
            ingredients.get(pos).setQuantity("");
    }

    public void setCheckedForIngredient(Boolean checked, int pos){
        ingredients.get(pos).setIsChecked(checked);
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public int getId(){return id;}
    public String getName() {
        return naam;
    }

    public String getContacts() {
        return contacten;
    }


    public List<Ingredient> getIngredients(){
        return ingredients;
    }

    public String getUrl() {
        return url;
    }

}