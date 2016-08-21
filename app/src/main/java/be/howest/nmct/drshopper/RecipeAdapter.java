package be.howest.nmct.drshopper;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import be.howest.nmct.drshopper.Admin.Models.Recipe;


public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private final List<Recipe> lstRecipes;
    Recipe r;

    public RecipeAdapter(Context context, List<Recipe> Recipes) {
        super(context, R.layout.row_recipe, R.id.tvNameRecipe, Recipes);
        this.lstRecipes = Recipes;
    }

    public void clearAdapter() {
        lstRecipes.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View row = super.getView(position, convertView, parent);
        r = lstRecipes.get(position);

        ViewHolder vh = (ViewHolder) row.getTag();

        if (vh == null) {
            vh = new ViewHolder(row);
            row.setTag(vh);
        }

        final ImageView imgRecipe = vh.imgRecipe;
        Picasso.with(getContext()).load(r.getURLFoto()).into(imgRecipe);

        TextView tvNameRecipe = vh.tvNameRecipe;
        tvNameRecipe.setText(r.getName());

        TextView tvDescriptionRecipe = vh.tvDescriptionRecipe;
        tvDescriptionRecipe.setText(r.getDescription());

        return row;
    }

    class ViewHolder {

        ImageView imgRecipe;
        TextView tvNameRecipe;
        TextView tvDescriptionRecipe;

        public ViewHolder(View row) {
            imgRecipe = (ImageView) row.findViewById(R.id.imgRecipe);
            tvNameRecipe = (TextView) row.findViewById(R.id.tvNameRecipe);
            tvDescriptionRecipe = (TextView) row.findViewById(R.id.tvDescriptionRecipe);
        }
    }


}