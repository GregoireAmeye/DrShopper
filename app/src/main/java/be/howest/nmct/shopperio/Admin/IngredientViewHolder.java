package be.howest.nmct.shopperio.Admin;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import be.howest.nmct.shopperio.R;

public class IngredientViewHolder extends RecyclerView.ViewHolder {
    protected CheckBox chkIngredient;
    protected TextView tvQuantity;
    public IngredientViewHolder(View itemView) {
        super(itemView);
        chkIngredient = (CheckBox) itemView.findViewById(R.id.rowIngredientName);
        tvQuantity = (TextView) itemView.findViewById(R.id.rowIngredientQuantity);

    }
}
