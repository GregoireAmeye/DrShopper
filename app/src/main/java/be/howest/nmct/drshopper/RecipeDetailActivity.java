package be.howest.nmct.drshopper;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import be.howest.nmct.drshopper.Admin.FavoritesDatabase;
import be.howest.nmct.drshopper.Admin.Globals;
import be.howest.nmct.drshopper.Admin.Models.Ingredient;
import be.howest.nmct.drshopper.Admin.Models.Recipe;
import be.howest.nmct.drshopper.Admin.SendIngredientsToShoplistAlert;
import be.howest.nmct.drshopper.Service.RecipeService;
import be.howest.nmct.drshopper.Service.ShoppingListService;
import be.howest.nmct.drshopper.Service.YummlyService;

public class RecipeDetailActivity extends AppCompatActivity implements SendIngredientsToShoplistAlert.onSendListener {

    public final static String EXTRA_RC = "be.howest.nmct.drshopper.RECIPE";
    public final static String EXTRA_YUMMLY = "be.howest.nmct.drshopper.ISYUMMLY";
    Recipe recipe = null;
    Toolbar myToolbar;
    Menu menu;
    FavoritesDatabase database;
    List<Recipe> lstFavoriteRecipes;
    boolean isYummly;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.clRecipeDetail);
        database = new FavoritesDatabase((getApplicationContext()));

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Recipe detail");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Globals g = Globals.getInstance();
        String token = g.getToken();

        Intent intent = getIntent();
        String id = intent.getStringExtra(EXTRA_RC);
        isYummly = intent.getBooleanExtra(EXTRA_YUMMLY, false);
        try {
            if (isYummly) {
                id = id.replace(" ", "%20").replace("'", "%27");
                recipe = YummlyService.SearchRecipe(id).get(0);
            } else {
                recipe = new RecipeService.getByIdAsync().execute(id).get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ImageView imgRecipe = (ImageView) findViewById(R.id.imgRecipe);
        Picasso.with(getBaseContext()).load(recipe.getURLFoto()).into(imgRecipe);

        myToolbar.setTitle(recipe.getName());

//        TextView tvNameList =(TextView) findViewById(R.id.tvNameRecipe);
//        tvNameList.setText(recipe.getName());

        TextView tvDescriptionRecipe = (TextView) findViewById(R.id.tvDescriptionRecipe);
        tvDescriptionRecipe.setText(recipe.getDescription());

        TextView tvInstructions = (TextView) findViewById(R.id.tvInstructionsRecipe);
        tvInstructions.setText(recipe.getInstruction());

        ListView lsvIngredients = (ListView) findViewById(R.id.lsvIngredients);
        lsvIngredients.setScrollContainer(false);
        List<Ingredient> lstIngredients = recipe.getIngredients();
        ArrayList<String> arrayIngredients = new ArrayList<>();
        for (Ingredient i : lstIngredients) {
            Log.d("FUJITORA", i.getName());
            if (i.getQuantity() != "") arrayIngredients.add(i.getName());
            else arrayIngredients.add(i.getName());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayIngredients) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextSize(15);
                text.setMinHeight(0); // Min Height
                text.setMinimumHeight(0); // Min Height
                text.setHeight(60); // Height
                text.setTextColor(Color.DKGRAY);


                return view;
            }
        };
        lsvIngredients.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_detail_recipe, menu);

        Intent intent = getIntent();
        boolean isYummly = intent.getBooleanExtra(EXTRA_YUMMLY, false);
        if (isYummly) {
            //menu.findItem(R.id.action_favourite_recipe).setVisible(false);
            menu.findItem(R.id.action_delete_recipe).setVisible(false);
            menu.findItem(R.id.action_edit_recipe).setVisible(false);
        }


        database.open();
        lstFavoriteRecipes = database.readAllFavorites();


        if (!lstFavoriteRecipes.isEmpty()) {
            for (Recipe r : lstFavoriteRecipes) {
                if (r != null) {
                    if (r.getName().equals(recipe.getName())) {
                        menu.findItem(R.id.action_favourite_recipe).setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                        menu.findItem(R.id.action_favourite_recipe).setChecked(true);
                    }
                }
            }
        }
        database.close();

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id) {
            case R.id.action_sendingredients:
                openShoppingListDialog();
                break;
            case R.id.action_favourite_recipe:
                toggleFavorite(item);
                break;
            case R.id.action_delete_recipe:
                deleteRecipe();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void deleteRecipe() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        RecipeService.deleteRecipe(recipe.getID());
                        Intent intent = new Intent(RecipeDetailActivity.this, RecipesActivity.class);
                        intent.putExtra("EXTRA_RC_DELETED", "RC_DELETED");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RecipeDetailActivity.this);
        builder
                .setMessage("Discard " + recipe.getName() + " recipe?")
                .setPositiveButton("Discard", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();

    }

    private void toggleFavorite(MenuItem item) {
        if (item.isChecked()) {
            database.open();
            if (isYummly) database.deleteFavorite(recipe.getName() + "");
            else database.deleteFavorite(recipe.getID() + "");
            database.close();
            item.setIcon(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Removed from favorites", Snackbar.LENGTH_SHORT);
            snackbar.show();
            item.setChecked(false);
        } else if (!item.isChecked()) {
            database.open();
            if (isYummly) database.addFavorite(recipe.getName() + "");
            else database.addFavorite(recipe.getID() + "");
            database.close();
            item.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Added to favorites", Snackbar.LENGTH_SHORT);
            snackbar.show();
            item.setChecked(true);
        }
    }

    private void openShoppingListDialog() {

        SendIngredientsToShoplistAlert alert = new SendIngredientsToShoplistAlert();
        alert.mListener = this;
        alert.show(getFragmentManager(), "");
    }


    @Override
    public void sendIngredientsToShoppinglist(int shoppinglistId, String shoppinglistName) {
        String ingr = "ingredient";
        if (recipe.getIngredients().size() != 1)
            ingr += "s";
        Snackbar
                .make(coordinatorLayout, "Added " + recipe.getIngredients().size() + " " + ingr + " to " + shoppinglistName, Snackbar.LENGTH_LONG)
                .show();
        //do this
        ShoppingListService.addIngredientsToShoppingList(recipe.getIngredients(), shoppinglistId);

    }
}
