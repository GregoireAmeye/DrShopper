package be.howest.nmct.shopperio.Admin;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import be.howest.nmct.shopperio.R;

public class IngredientRecipeViewHolder extends RecyclerView.ViewHolder{
    protected TextView tvIngredient;
    protected TextView tvQuantity;
    public IngredientRecipeViewHolder(View itemView) {
        super(itemView);
        tvIngredient = (TextView) itemView.findViewById(R.id.rowIngrName);
        tvQuantity = (TextView) itemView.findViewById(R.id.rowIngrQuan);

    }
}
