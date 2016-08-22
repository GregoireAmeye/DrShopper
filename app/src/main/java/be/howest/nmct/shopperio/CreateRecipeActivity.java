package be.howest.nmct.shopperio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.solovyev.android.views.llm.DividerItemDecoration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import be.howest.nmct.shopperio.Admin.AddIngredientRecipeAlert;
import be.howest.nmct.shopperio.Admin.IngredientRecipeCreateAdapter;
import be.howest.nmct.shopperio.Admin.Models.Ingredient;
import be.howest.nmct.shopperio.Admin.Models.Recipe;
import be.howest.nmct.shopperio.Service.RecipeService;

public class CreateRecipeActivity extends AppCompatActivity implements AddIngredientRecipeAlert.OnAddListener {
    public static final int FILE_SELECT_CODE = 1;
    public static List<Recipe> lstRecipes = null;
    ImageView imgPic;
    Uri selectedImageUri = null;
    EditText etRecipeName;
    EditText etDescription;
    EditText etInstructions;
    TextView tvAddIngredient;
    Button btnCreateRecipe;
    FloatingActionButton fabAddImage;
    Toolbar myToolbar;
    IngredientRecipeCreateAdapter mAdapter;
    FloatingActionButton fab;
    List<Ingredient> ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Create your recipe");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvAddIngredient = (TextView) findViewById(R.id.tvAddIngr);

        tvAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupForNewIngr();
            }
        });

        //Keyboard.toggle(this);
/*
        fab = (FloatingActionButton) findViewById(R.id.fabAddIngrRecipe);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupForNewIngr();
            }
        });

        */

        final LinearLayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rclIngredientsRecipe);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));
        ingredients = new ArrayList<>();
        mAdapter = new IngredientRecipeCreateAdapter(ingredients);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(300);
        itemAnimator.setRemoveDuration(1000);
        itemAnimator.setMoveDuration(300);
        itemAnimator.setChangeDuration(300);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(mAdapter);

        imgPic = (ImageView) findViewById(R.id.imgRecipe);
        fabAddImage = (FloatingActionButton) findViewById(R.id.fabAddImage);
        fabAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileExplorer();
            }
        });

        etRecipeName = (EditText) findViewById(R.id.etRecipeName);


        etDescription = (EditText) findViewById(R.id.etRecipeDescription);
        etInstructions = (EditText) findViewById(R.id.etRecipeInstructions);
    }

    private void showPopupForNewIngr() {
        //  ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        AddIngredientRecipeAlert alert = new AddIngredientRecipeAlert();
        alert.mListener = this;
        alert.show(getFragmentManager(), "");
    }

    private void createRecipe() {
        // Keyboard.toggle(this);
        try {
            if (selectedImageUri != null) {

                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                boolean test = new RecipeService.createNewRecipe().execute(etRecipeName.getText().toString(), etDescription.getText().toString(), etInstructions.getText().toString(), ingredients, bm).get();
            } else {
                boolean test = new RecipeService.createNewRecipe().execute(etRecipeName.getText().toString(), etDescription.getText().toString(), etInstructions.getText().toString(), ingredients).get();
            }
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
        } catch (ExecutionException ex) {
        }
    }

    private void openFileExplorer() {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, FILE_SELECT_CODE);

        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, FILE_SELECT_CODE);
        }
    }

    private void goToRecipesActivity() {
        Intent myIntent = new Intent(CreateRecipeActivity.this, RecipesActivity.class);

        CreateRecipeActivity.this.startActivity(myIntent);
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    selectedImageUri = data.getData();
                    setBitMapImageview(selectedImageUri);
                    break;
            }
        }

    }

    private void setBitMapImageview(Uri selectedImageUri) {
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            bm = Bitmap.createScaledBitmap(bm, 250, 250, true);
            imgPic.setImageBitmap(bm);
        } catch (FileNotFoundException ex) {
            Log.e(ex.getClass().getName(), ex.getMessage());
        } catch (IOException ex) {
            Log.e(ex.getClass().getName(), ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_recipe) {
            if (etRecipeName.getText().toString().equals("")) {
                etRecipeName.setError("Required");
            } else {
                final ProgressDialog progressDialog = ProgressDialog.show(CreateRecipeActivity.this, "Please wait", "Creating recipe...", true);
                progressDialog.setCancelable(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        createRecipe();
                        goToRecipesActivity();
                        progressDialog.dismiss();
                    }
                }).start();
            }


        }

        return super.onOptionsItemSelected(item);
    }

    /*
        @Override
        public void addNewIngredient(String ingredientName, String quanitity, String measure) {
            ingredientName = sanatizeParameter(ingredientName);
            quanitity = sanatizeParameter(quanitity);
            measure = sanatizeParameter(measure);
            Ingredient i = new Ingredient(ingredientName,quanitity,measure);

            ingredientName = ingredientName.replaceAll("%20", " ");

            i.setIsChecked(false);
            addNewIngredientToList(i);
        }
    */
    private void addNewIngredientToList(Ingredient i) {
        ingredients.add(i);
        mAdapter.notifyDataSetChanged();
    }

    private String sanatizeParameter(String param) {
        param = param.trim();
        param = param.replaceAll(" ", "%20");
        return param;
    }

    @Override
    public void addNewIngredient(String ingredientName) {
        ingredientName = sanatizeParameter(ingredientName);
        Ingredient i = new Ingredient(ingredientName, "", "");


        i.setIsChecked(false);
        addNewIngredientToList(i);

    }
}
