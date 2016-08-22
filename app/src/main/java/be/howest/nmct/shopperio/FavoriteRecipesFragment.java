package be.howest.nmct.shopperio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import be.howest.nmct.shopperio.Admin.FavoritesDatabase;
import be.howest.nmct.shopperio.Admin.Models.Recipe;


public class FavoriteRecipesFragment extends Fragment {

    private List<Recipe> recipes = new ArrayList<>();


    public static FavoriteRecipesFragment newInstance() {
        FavoriteRecipesFragment fragment = new FavoriteRecipesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipes_favorites, container, false);
        FavoritesDatabase database = new FavoritesDatabase(getContext());
        try {
            database.open();

            recipes.clear();
            recipes = database.readAllFavorites();
            database.close();
        } catch (Exception ex) {

        }


        if (recipes.size() != 0) {
            RelativeLayout warning = (RelativeLayout) v.findViewById(R.id.warningNoRecipes);
            warning.setVisibility(View.GONE);
        }

        ListView lst = (ListView) v.findViewById(R.id.lstRecipes);
        RecipeAdapter adapter = new RecipeAdapter(getActivity(), recipes);
        lst.setAdapter(adapter);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                if (recipes.get(position).getID() != 0) {
                    intent.putExtra(RecipeDetailActivity.EXTRA_RC, "" + recipes.get(position).getID());
                } else {
                    intent.putExtra(RecipeDetailActivity.EXTRA_RC, "" + recipes.get(position).getName());
                    intent.putExtra(RecipeDetailActivity.EXTRA_YUMMLY, true);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return v;
    }
}
