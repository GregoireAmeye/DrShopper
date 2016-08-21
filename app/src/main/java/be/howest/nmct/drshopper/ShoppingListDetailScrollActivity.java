package be.howest.nmct.drshopper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import be.howest.nmct.drshopper.Admin.AddIngredientAlert;
import be.howest.nmct.drshopper.Admin.CheckIngredientsHelper;
import be.howest.nmct.drshopper.Admin.Globals;
import be.howest.nmct.drshopper.Admin.Models.CheckedIngredientsContract;
import be.howest.nmct.drshopper.Admin.Models.Ingredient;
import be.howest.nmct.drshopper.Admin.Models.Keyboard;
import be.howest.nmct.drshopper.Admin.Models.Quantity;
import be.howest.nmct.drshopper.Admin.Models.ShoppingList;
import be.howest.nmct.drshopper.Admin.OfflineIngredientDatabase;
import be.howest.nmct.drshopper.Admin.ShoppingListIngredientAdapter;
import be.howest.nmct.drshopper.Service.ShoppingListService;

public class ShoppingListDetailScrollActivity extends AppCompatActivity implements AddIngredientAlert.OnAddListener, SensorEventListener {
    public final static String EXTRA_SL = "be.howest.nmct.drshopper.SHOPPINGLIST";
    public final static String EXTRA_SL_NAME = "be.howest.nmct.drshopper.SHOPPINGLIST.name";
    ShoppingList sl = null;
    List<Quantity> quantities = null;
    List<Ingredient> ingredients = null;
    ShoppingListIngredientAdapter mAdapter = null;
    private SensorManager mSensorManager;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;

    private Sensor mSensor;

    private static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_detail_scroll);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        try {
            // Yeah, this is hidden field.
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());


        Globals g = Globals.getInstance();
        String token = g.getToken();

        Intent intent = getIntent();
        int id = intent.getIntExtra(EXTRA_SL, 0);


        if (isNetworkAvailable()) {
            try {
                sl = new ShoppingListService.getByIdAsync().execute("" + id).get();
                quantities = new ShoppingListService.getQuantities().execute("" + sl.getId()).get();
                matchListWithQuantities();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cacheIngredients();
                    }
                }).start();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            sl = new ShoppingList();
            String shoplistname = intent.getStringExtra(EXTRA_SL_NAME);
            sl.setId(id);
            sl.setNaam(shoplistname);

            getIngredientsFromCache();


        }


        checkForCheckedIngredients();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rclIngredients);


        recyclerView.setLayoutManager(layoutManager);

        ingredients = sl.getIngredients();
        mAdapter = new ShoppingListIngredientAdapter(ingredients);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(300);
        itemAnimator.setRemoveDuration(1000);
        itemAnimator.setMoveDuration(300);
        itemAnimator.setChangeDuration(300);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(mAdapter);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(sl.getName());


        ImageView imgList = (ImageView) findViewById(R.id.backdrop);
        if (sl.getUrl() != null)
            Picasso.with(getBaseContext()).load(sl.getUrl()).into(imgList);
        else
            Picasso.with(getBaseContext()).load(R.drawable.list2).into(imgList);

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new ShoppingListService.addIngredientToList().execute();
                showPopupAddIngredient();
            }
        });
    }

    private void cacheIngredients() {
        OfflineIngredientDatabase database = new OfflineIngredientDatabase(this);
        database.open();
        database.cacheIngredients(sl.getIngredients(), sl.getId());
        database.close();
    }

    private void getIngredientsFromCache() {
        OfflineIngredientDatabase database = new OfflineIngredientDatabase(this);
        database.open();
        sl.setIngredients(database.getAllIngredientsFromShoppinglist(sl.getId()));
        database.close();
        if (sl.getIngredients().size() == 0) {
            final View coordinatorLayoutView = findViewById(R.id.shoplistdetail);


            Snackbar
                    .make(coordinatorLayoutView, "There were no ingredients cached!", Snackbar.LENGTH_LONG)
                    .show();

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void checkForCheckedIngredients() {

        CheckIngredientsHelper mDbHelper = new CheckIngredientsHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_INGREDIENT_ID,
                CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_CHECKED,
                CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_SHOPPINGLIST_ID
        };
        HashMap<Integer, Boolean> checkedIngredients = new HashMap<>();

        Cursor c = db.query(CheckedIngredientsContract.IngredientEntry.TABLE_NAME, projection, null, null, null, null, null);

        while (c.moveToNext()) {
            String ingredientId = c.getString(c.getColumnIndexOrThrow(CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_INGREDIENT_ID));
            String checked = c.getString(c.getColumnIndexOrThrow(CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_CHECKED));
            String shoppinglistId = c.getString(c.getColumnIndexOrThrow(CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_SHOPPINGLIST_ID));
            int shoplistid = Integer.parseInt(shoppinglistId);
            if (shoplistid == sl.getId()) {
                if (checked.equals("true")) {
                    checkedIngredients.put(Integer.parseInt(ingredientId), true);
                    Log.w("test", ingredientId + " is true");
                } else {
                    checkedIngredients.put(Integer.parseInt(ingredientId), false);
                }
            }

        }
        db.close();


        checkCheckedItems(checkedIngredients);
    }

    private void checkCheckedItems(HashMap<Integer, Boolean> checkedIngredients) {
        for (Map.Entry<Integer, Boolean> entry : checkedIngredients.entrySet()) {
            for (int ii = 0; ii < sl.getIngredients().size(); ii++) {
                if (entry.getKey() == sl.getIngredients().get(ii).getID()) {
                    sl.setCheckedForIngredient(entry.getValue(), ii);
                }
            }
        }
    }

    private void showPopupAddIngredient() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        AddIngredientAlert alert = new AddIngredientAlert();
        alert.mListener = this;
        alert.show(getFragmentManager(), "");

    }

    private void matchListWithQuantities() {
        for (int i = 0; i < quantities.size(); i++) {
            Quantity q = quantities.get(i);
            for (int ii = 0; ii < sl.getIngredients().size(); ii++) {
                if (q.getIngredientId() == sl.getIngredients().get(ii).getID()) {
                    sl.setQuantityForIngredient(q, ii);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shoplistdetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println(item.getItemId());
        switch (item.getItemId()) {
            case R.id.action_delete:
                promptDialog();
                break;
            case R.id.menu_item_share:
                smsList();
                break;
            case 16908332:
                onBackPressed();
                break;
            case R.id.action_clearall:
                clearAllCheckedIngredients();
                break;
            default:
                break;

        }
        return true;
    }

    private void clearAllCheckedIngredients() {
        List<Ingredient> checkedIngredients = new ArrayList<>();
        List<Ingredient> allIngredients = new ArrayList<>();
        allIngredients.addAll(ingredients);
        Iterator<Ingredient> iterator = allIngredients.iterator();


        while (iterator.hasNext()) {
            Ingredient i = iterator.next();
            if (i.isChecked) {
                checkedIngredients.add(i);
                int position = sl.getIngredients().indexOf(i);

                mAdapter.removeAt(position);


            }
        }
        final View coordinatorLayoutView = findViewById(R.id.shoplistdetail);

        String variable = "ingredient";
        int size = checkedIngredients.size();
        if (size != 1)
            variable += "s";


        Snackbar
                .make(coordinatorLayoutView, size + " " + variable + " cleared.", Snackbar.LENGTH_LONG)
                .show();
        ShoppingListService.clearIngredients(checkedIngredients);

    }

    private void smsList() {
        if (sl.getIngredients().size() == 0) {
            final View coordinatorLayoutView = findViewById(R.id.shoplistdetail);

            Snackbar
                    .make(coordinatorLayoutView, "No ingredients in this list!", Snackbar.LENGTH_LONG)
                    .show();
        } else {


            Intent smsIntent = new Intent(Intent.ACTION_SEND);

            String smsBody = "";
            for (Ingredient i : sl.getIngredients()) {
                smsBody += i.getName();
                if (i.getQuantityName() != null) {
                    smsBody += " " + i.getQuantity() + " " + i.getQuantityName();
                }
                smsBody += "\n";
            }
            smsIntent.setType("text/plain");
            smsIntent.putExtra(Intent.EXTRA_TEXT, smsBody);
            startActivity(Intent.createChooser(smsIntent, "Share with"));
        }

    }

    private void promptDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteThisList();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //no
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingListDetailScrollActivity.this);
        builder.setMessage("Discard shoppinglist " + sl.getName() + "?").setPositiveButton("Discard", dialogClickListener).setNegativeButton("Cancel", dialogClickListener).show();


    }


    private void deleteThisList() {
        ShoppingListService.deleteShoppingList(sl.getId());
        Intent intent = new Intent(ShoppingListDetailScrollActivity.this, ShoppingListsActivity.class);
        startActivity(intent);
    }

    @Override
    public void addNewIngredient(String ingredientName, String quanitity, String measure) {
        ingredientName = sanatizeParameter(ingredientName);
        quanitity = sanatizeParameter(quanitity);
        measure = sanatizeParameter(measure);
        try {
            Boolean result = new ShoppingListService.addIngredientToList(ingredientName, quanitity, measure, sl.getId()).execute().get();
        } catch (InterruptedException ex) {

        } catch (ExecutionException ex) {

        }

        ingredientName = ingredientName.replaceAll("%20", " ");
        Ingredient ingr = new Ingredient(ShoppingListService.ResponseIngredientId, ingredientName, measure, quanitity);
        ingr.setIsChecked(false);
        addNewIngredientToList(ingr);
        hideKeyboard();
    }

    private void hideKeyboard() {
        Keyboard.toggle(this);
    }

    private void addNewIngredientToList(Ingredient ingr) {

        ingredients.add(0, ingr);
        mAdapter.notifyItemInserted(0);

        /*
        ingredients.add(ingr);
        mAdapter.notifyItemInserted(ingredients.size() - 1);
         */

    }

    private String sanatizeParameter(String param) {
        param = param.trim();
        param = param.replaceAll(" ", "%20");
        return param;
    }

    @Override
    protected void onPause() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                writeCheckedToSqlite();
                cacheIngredients();
            }
        }).start();
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void writeCheckedToSqlite() {
        CheckIngredientsHelper mDbHelper = new CheckIngredientsHelper(this);
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys


        // Define 'where' part of query.
        String selection = CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_SHOPPINGLIST_ID + " LIKE ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(sl.getId())};
// Issue SQL statement.
        db.delete(CheckedIngredientsContract.IngredientEntry.TABLE_NAME, selection, selectionArgs);


        for (Ingredient i : sl.getIngredients()) {
            ContentValues values = new ContentValues();
            if (i.isChecked != null) {
                if (i.getIsChecked()) {
                    values.put(CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_INGREDIENT_ID, i.getID());
                    values.put(CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_CHECKED, "true");
                    values.put(CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_SHOPPINGLIST_ID, sl.getId());
                    Log.w("test", i.getName() + " set to true");
                } else {
                    values.put(CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_INGREDIENT_ID, i.getID());
                    values.put(CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_CHECKED, "false");
                    values.put(CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_SHOPPINGLIST_ID, sl.getId());
                }
            } else {
                values.put(CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_INGREDIENT_ID, i.getID());
                values.put(CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_CHECKED, "false");
                values.put(CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_SHOPPINGLIST_ID, sl.getId());
            }

            db.insert(
                    CheckedIngredientsContract.IngredientEntry.TABLE_NAME, null,
                    values);
        }

        db.close();


    }

    @Override
    public void onBackPressed() {
        writeCheckedToSqlite();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 0) {
            turnOffScreen();
        } else {
            turnOnScreen();
        }
    }

    public void turnOnScreen() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }


    public void turnOffScreen() {
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
