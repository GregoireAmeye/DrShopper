package be.howest.nmct.drshopper;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import be.howest.nmct.drshopper.Admin.Models.Recipe;
import be.howest.nmct.drshopper.Service.YummlyService;


public class YourRecipesFragment extends Fragment {

    public static List<Recipe> Recipes = new ArrayList<>();
    public static List<Recipe> RecipesWinter = null;
    public static List<Recipe> RecipesSummer = null;
    public static List<Recipe> RecipesSpring = null;
    public static List<Recipe> RecipesFall = null;
    List<Recipe> seasonRecipes = null;

    RecipeAdapter mAdapter = null;
    ListView lstRecipes = null;
    ListView lstRecipesWinter = null;
    ListView lstRecipesSummer = null;
    ListView lstRecipesSpring = null;
    ListView lstRecipesFall = null;

    public static YourRecipesFragment newInstance() {
        YourRecipesFragment fragment = new YourRecipesFragment();
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
        View v = inflater.inflate(R.layout.fragment_recipes_yourrecipes, container, false);


        if (Recipes != null) Recipes.clear();


        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);

        month += 1;

        String season = getSeason(month);

        seasonRecipes = YummlyService.SearchRecipes(season);

        Recipes.addAll(seasonRecipes);

        if (Recipes.size() != 0) {
            RelativeLayout warning = (RelativeLayout) v.findViewById(R.id.warningNoRecipes);
            warning.setVisibility(View.GONE);
        }

        lstRecipes = (ListView) v.findViewById(R.id.lstRecipes);
        //lstRecipesWinter = (ListView) v.findViewById(R.id.lstRecipesWinter);
        //lstRecipesSummer = (ListView) v.findViewById(R.id.lstRecipesSummer);
        //lstRecipesSpring = (ListView) v.findViewById(R.id.lstRecipesSpring);
        //lstRecipesFall = (ListView) v.findViewById(R.id.lstRecipesFall);

        try {
            mAdapter = new RecipeAdapter(getActivity(), Recipes);
            lstRecipes.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            lstRecipes.setAdapter(null);
        }


        lstRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.EXTRA_RC, Recipes.get(position).getName());
                intent.putExtra(RecipeDetailActivity.EXTRA_YUMMLY, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        return v;
    }

    String getSeason(int month) {
        switch (month) {
            case 11:
            case 12:
            case 1:
            case 2:
                return "winter";
            case 3:
            case 4:
                return "spring";
            case 5:
            case 6:
            case 7:
            case 8:
                return "summer";
            default:
                return "autumn";
        }
    }
}
