package be.howest.nmct.drshopper.Admin;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import be.howest.nmct.drshopper.R;

public class IngredientRecipeViewHolder extends RecyclerView.ViewHolder{
    protected TextView tvIngredient;
    protected TextView tvQuantity;
    public IngredientRecipeViewHolder(View itemView) {
        super(itemView);
        tvIngredient = (TextView) itemView.findViewById(R.id.rowIngrName);
        tvQuantity = (TextView) itemView.findViewById(R.id.rowIngrQuan);

    }
}