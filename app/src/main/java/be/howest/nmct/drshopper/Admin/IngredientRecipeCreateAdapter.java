package be.howest.nmct.drshopper.Admin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import be.howest.nmct.drshopper.Admin.Models.Ingredient;
import be.howest.nmct.drshopper.R;

public class IngredientRecipeCreateAdapter extends RecyclerView.Adapter<IngredientRecipeViewHolder> {

    private List<Ingredient> ingredients;


    public IngredientRecipeCreateAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public IngredientRecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ingredient_recipe_add, parent, false);
        return new IngredientRecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(IngredientRecipeViewHolder holder, int position) {
        final Ingredient i = ingredients.get(position);

        holder.tvQuantity.setText(i.getQuantity() + " " + i.getQuantityName());
        holder.tvIngredient.setText(i.getName());
    }

    @Override
    public int getItemCount() {
       return ingredients.size();
    }
}
