package be.howest.nmct.shopperio.Admin.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicolas on 09-Dec-15.
 */
public class Recipe {
    private int ID;
    private String Name;
    private String URLFoto;
    private String Description;
    private String Instruction;
    private List<Ingredient> Ingredients;

    public Recipe() {
        Ingredients = new ArrayList<>();
        Description = "No Description";
        Instruction = "No Instructions";
    }

    public Recipe(int id, String name, String urlFoto, String description, String instruction, List<Ingredient> ingredients) {
        ID = id;
        Name = name;
        if (urlFoto != "" && urlFoto != null && urlFoto != "null") URLFoto = urlFoto;
        else
            URLFoto = "https://d1hekt5vpuuw9b.cloudfront.net/assets/article/b7812de1163a8890bc10f36298ed7906_make-your-own-heirloom-cookbook-lede-580x326_featuredImage.jpg";
        Description = description;
        Instruction = instruction;
        Ingredients = ingredients;
    }

    ;

    public List<Ingredient> getIngredients() {
        return Ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        Ingredients = ingredients;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getURLFoto() {
        return URLFoto;
    }

    public void setURLFoto(String URLFoto) {
        this.URLFoto = URLFoto;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getInstruction() {
        return Instruction;
    }

    public void setInstruction(String instruction) {
        Instruction = instruction;
    }

    public void addIngredient(Ingredient ingredient) {
        Ingredients.add(ingredient);
    }
}
