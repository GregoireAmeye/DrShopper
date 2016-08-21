package be.howest.nmct.drshopper.Admin;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import be.howest.nmct.drshopper.Admin.Models.Ingredient;
import be.howest.nmct.drshopper.R;

public class ShoppingListIngredientAdapter extends RecyclerView.Adapter<IngredientViewHolder>{
    private List<Ingredient> ingredients;

    public ShoppingListIngredientAdapter(List<Ingredient> ingredients){
        this.ingredients = ingredients;
        sortList();
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ingredient, parent, false);
        return new IngredientViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final IngredientViewHolder holder, final int position) {
        final Ingredient i = ingredients.get(position);
        holder.tvQuantity.setText(i.getQuantity() + " " + i.getQuantityName());
        holder.chkIngredient.setText(i.getName());


        holder.chkIngredient.setOnCheckedChangeListener(null);
        if(i.isChecked!=null){
            holder.chkIngredient.setChecked(i.getIsChecked());
        }
        else{
            i.isChecked = false;
            holder.chkIngredient.setChecked(false);
        }


        holder.chkIngredient.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                i.setIsChecked(isChecked);

                /*
                if(isChecked){
                    ingredients.remove(position);
                    ingredients.add(ingredients.size()-1,i);
                    notifyItemMoved(position,ingredients.size()-1);
                }else if(!isChecked){

                    ingredients.remove(position);
                    ingredients.add(0,i);
                    notifyItemMoved(position,0);

                }
                */

                sortList();
            }
        });

        /*
        holder.chkIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.w("test", ingredients.get(position).getName() + " clicked");

                if (holder.chkIngredient.isChecked())
                    ingredients.get(position).setIsChecked(true);
                else
                    ingredients.get(position).setIsChecked(false);

                //notifyDataSetChanged();
                //customSort();
            }
        });
        */


    }


    private void sortList() {
        Collections.sort(ingredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient abc1, Ingredient abc2) {
                return Boolean.compare(abc1.isChecked, abc2.isChecked);
            }
        });

        notifyDataSetChanged();
    }




    public boolean removeItem(int position) {
        if (ingredients.size() >= position + 1) {
            ingredients.remove(position);
            return true;
        }
        return false;
    }


    public void removeAt(int position) {
        ingredients.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ingredients.size());
    }





    @Override
    public int getItemCount() {
        return ingredients.size();
    }



}
