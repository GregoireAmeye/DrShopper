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
import java.util.concurrent.ExecutionException;

import be.howest.nmct.shopperio.Admin.Models.Recipe;
import be.howest.nmct.shopperio.Service.RecipeService;


public class AllRecipesFragment extends Fragment {

    public static List<Recipe> lstRecipes = new ArrayList<>();
    RecipeAdapter mAdapter = null;
    ListView lst = null;

    public static AllRecipesFragment newInstance() {
        AllRecipesFragment fragment = new AllRecipesFragment();
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
        View v = inflater.inflate(R.layout.fragment_recipes_all, container, false);

        try {
            if (lstRecipes != null) lstRecipes.clear();
            lstRecipes = new RecipeService.getRecipesAsync().execute().get();

            if (lstRecipes != null) {
                if (lstRecipes.size() != 0) {
                    RelativeLayout warning = (RelativeLayout) v.findViewById(R.id.warningNoRecipes);
                    warning.setVisibility(View.GONE);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        lst = (ListView) v.findViewById(R.id.lstRecipes);
        try {
            mAdapter = new RecipeAdapter(getActivity(), lstRecipes);
            lst.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            lst.setAdapter(null);
        }


        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.EXTRA_RC, "" + lstRecipes.get(position).getID());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return v;
    }
}
