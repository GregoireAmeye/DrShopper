package be.howest.nmct.drshopper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import be.howest.nmct.drshopper.Admin.Models.Keyboard;
import be.howest.nmct.drshopper.Admin.SendIngredientsToShoplistAlert;
import be.howest.nmct.drshopper.Service.ShoppingListService;

public class CreateShopListActivity extends AppCompatActivity {
    public static final int FILE_SELECT_CODE = 1;
    ImageView imgPic;
    Bitmap newImage = null;
    EditText etShoplistName;
    Button btnCreateShoppinglist;
    TextView tvError;
    FloatingActionButton fab = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shop_list_nice);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarshoplistnice);
        myToolbar.setTitle("Create your shopping list");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fabAddImage);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });


        imgPic = (ImageView) findViewById(R.id.imgShoppingListSelectNice);
        /*
        imgPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileExplorer();
            }
        });
        */

        //tvError = (TextView) findViewById(R.id.tvErrorCreateShopList);

        etShoplistName = (EditText) findViewById(R.id.etShoppingListNameNice);

        /*
        btnCreateShoppinglist = (Button) findViewById(R.id.btnCreateShoppingList);
        btnCreateShoppinglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = ProgressDialog.show(CreateShopListActivity.this, "Please wait", "Creating shopping list...", true);
                progressDialog.setCancelable(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        createShoppingList();
                        goToShoppingListActivity();
                        progressDialog.dismiss();
                    }
                }).start();

            }
        });
        */
        Keyboard.toggle(this);

    }


    private void goToShoppingListActivity() {
        Intent myIntent = new Intent(CreateShopListActivity.this, ShoppingListsActivity.class);

        CreateShopListActivity.this.startActivity(myIntent);
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }


    private void createShoppingList() {
        Keyboard.toggle(this);
        try {
            if (newImage != null) {

                boolean test = new ShoppingListService.createNewShoppingListb().execute(etShoplistName.getText().toString(), newImage).get();

            } else {
                boolean test = new ShoppingListService.createNewShoppingListb().execute(etShoplistName.getText().toString()).get();


            }

        } catch (InterruptedException ex) {

        } catch (ExecutionException ex) {

        }


    }
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    selectedImageUri = data.getData();
                    setBitMapImageview(selectedImageUri);
                    break;
            }
        }*/
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            newImage = (Bitmap) extras.get("data");
            imgPic.setImageBitmap(newImage);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_recipe, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_recipe) {
            final ProgressDialog progressDialog = ProgressDialog.show(CreateShopListActivity.this, "Please wait", "Creating shopping list...", true);
            progressDialog.setCancelable(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    createShoppingList();
                    goToShoppingListActivity();
                    progressDialog.dismiss();
                }
            }).start();

        }

        return super.onOptionsItemSelected(item);
    }


}
