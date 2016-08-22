package be.howest.nmct.shopperio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import be.howest.nmct.shopperio.Admin.Globals;
import be.howest.nmct.shopperio.Admin.Models.ShoppingList;
import be.howest.nmct.shopperio.Admin.OfflineShoppinglistDatabase;
import be.howest.nmct.shopperio.Service.ShoppingListService;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingListsActivity extends AppCompatActivity {


    public static List<ShoppingList> ShoppingLists = null;
    TextView tvUsername;
    Context c = null;
    ListView lst = null;
    ListAdapter mAdapter = null;
    FloatingActionButton fabCreateShoppingList;
    private int selectedItemIds[];
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private AdView adView;

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap", "returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglists);

        //using singleton
        Globals g = Globals.getInstance();
        String email = g.getEmail();

        c = this;

        //Locate the Banner Ad in activity_main.xml
        adView = (AdView) this.findViewById(R.id.adView);
        // Request for Ads
        AdRequest adRequest = new AdRequest.Builder()
                // Add a test device to show Test Ads
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("CC5F2C72DF2B356BBF0DA198")
                .build();

        // Load ads into Banner Ads
        adView.loadAd(adRequest);


        lst = (ListView) findViewById(R.id.lstShoppingList);


        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(R.id.ShoppingLists);
        View header = navigationView.getHeaderView(0);
        tvUsername = (TextView) header.findViewById(R.id.tvUsernameDrawer);
        tvUsername.setText(g.getUsername());
        ImageView imgProfile = (ImageView) header.findViewById(R.id.imgProfile);
        imgProfile.setImageBitmap(getBitmapFromURL(g.getFotoUrl()));


        fabCreateShoppingList = (FloatingActionButton) findViewById(R.id.fabAddNewShoppinglist);
        fabCreateShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewShoppingList();
            }
        });
        if (isNetworkAvailable()) {
            try {
                if (ShoppingLists != null) ShoppingLists.clear();
                ShoppingLists = new ShoppingListService.getListsAsync().execute().get();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cacheShoppingLists();
                    }
                }).start();


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {

            getShoppingListsFromCache();


        }


        if (ShoppingLists != null) {
            if (ShoppingLists.size() != 0) {
                RelativeLayout warning = (RelativeLayout) findViewById(R.id.warningNoLists);
                warning.setVisibility(View.GONE);
            }
        }

        try {

            mAdapter = new ListAdapter(getBaseContext(), ShoppingLists);


            lst.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            lst.setAdapter(null);
        }

        lst.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lst.setMultiChoiceModeListener(new MultiChoiceModeListener());

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ShoppingListDetailScrollActivity.class);
                intent.putExtra(ShoppingListDetailActivity.EXTRA_SL, ShoppingLists.get(position).getId());
                intent.putExtra(ShoppingListDetailScrollActivity.EXTRA_SL_NAME, ShoppingLists.get(position).getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        });


        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Checking if the item is in checked state or not, if not make it in checked state
                menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {


                    // For rest of the options we just show a toast on click
                    case R.id.ShoppingLists:
                        Intent ShopListIntent = new Intent(getBaseContext(), ShoppingListsActivity.class);
                        startActivity(ShopListIntent);
                        finish();
                        return true;
                    case R.id.Recipes:
                        Intent RecipeIntent = new Intent(getBaseContext(), RecipesActivity.class);
                        startActivity(RecipeIntent);
                        finish();
                        return true;

                    /*case R.id.Account:
                        Intent ProfileIntent = new Intent(getBaseContext(), ProfileActivity.class);
                        ProfileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(ProfileIntent);
                        finish();
                        return true;*/
                    case R.id.logout:
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
                                        prefs.edit().clear().commit();

                                        Intent LoginIntent = new Intent(getBaseContext(), LoginActivity.class);
                                        startActivity(LoginIntent);
                                        finish();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        navigationView.setCheckedItem(R.id.ShoppingLists);
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(c);
                        builder.setMessage("Are you sure you want to log out?").setPositiveButton("logout", dialogClickListener)
                                .setNegativeButton("cancel", dialogClickListener).show();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        fragmentTransaction.commit();
                        return true;

                }
            }
        });


        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {


            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


        //change headerview
        //((TextView) findViewById(R.id.tvUsernameDrawer)).setText(g.getUsername());


    }

    private void getShoppingListsFromCache() {
        OfflineShoppinglistDatabase db = new OfflineShoppinglistDatabase(this);
        db.open();
        ShoppingLists = db.getAllShoppinglists();
        db.close();
    }

    private void cacheShoppingLists() {
        OfflineShoppinglistDatabase db = new OfflineShoppinglistDatabase(this);
        db.open();
        db.saveAllShoppinglists(ShoppingLists);
        db.close();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    Intent intent = new Intent(ShoppingListsActivity.this, SettingsActivity.class);
//
        //    startActivity(intent);
//
        //}

        return super.onOptionsItemSelected(item);
    }


    private void CreateNewShoppingList() {
        Intent myIntent = new Intent(ShoppingListsActivity.this, CreateShopListActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myIntent);
    }

    @Override
    protected void onPause() {
        if (adView != null)
            adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null)
            adView.resume();
    }

    @Override
    protected void onDestroy() {
        if (adView != null)
            adView.destroy();
        super.onDestroy();
    }

    public class MultiChoiceModeListener implements ListView.MultiChoiceModeListener {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_shoppinglists, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.delete) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                // Calls getSelectedIds method from ListViewAdapter Class
                                SparseBooleanArray selected = mAdapter
                                        .getSelectedIds();
                                // Captures all selected ids with a loop
                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                    if (selected.valueAt(i)) {
                                        ShoppingList selecteditem = mAdapter
                                                .getItem(selected.keyAt(i));
                                        // Remove selected items following the ids
                                        ShoppingLists.remove(selecteditem);

                                        ShoppingListService.deleteShoppingList(selecteditem.getId());

                                    }
                                }

                                // Close CAB
                                Intent myIntent = new Intent(ShoppingListsActivity.this, ShoppingListsActivity.class);
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(myIntent);
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                navigationView.setCheckedItem(R.id.Recipes);
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                String items;
                if (mAdapter.getSelectedIds().size() == 1) items = " items?";
                else items = " item?";
                builder.setMessage("Do you really want to delete " + mAdapter
                        .getSelectedIds().size() + items).setPositiveButton("delete", dialogClickListener)
                        .setNegativeButton("cancel", dialogClickListener).show();
                return true;
            }
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {

            mAdapter.removeSelection();
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                              long id, boolean checked) {
            // Capture total checked items
            final int checkedCount = lst.getCheckedItemCount();
            // Set the CAB title according to total checked items
            mode.setTitle(checkedCount + " Selected");

            // Calls toggleSelection method from ListViewAdapter Class
            mAdapter.toggleSelection(position);
        }

    }

}

