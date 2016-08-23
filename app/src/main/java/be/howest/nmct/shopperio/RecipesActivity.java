package be.howest.nmct.shopperio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import be.howest.nmct.shopperio.Admin.Globals;
import be.howest.nmct.shopperio.Tabs.SlidingTabLayout;
import be.howest.nmct.shopperio.Tabs.ViewPagerAdapter;


public class RecipesActivity extends ActionBarActivity {
    public String EXTRA_RC_TO_DELETE;
    TextView tvUsername;
    Toolbar toolbar;
    ViewPager pager;
    SlidingTabLayout tabs;
    ViewPagerAdapter adapter;
    FloatingActionButton fabAddNewRecipe;
    CoordinatorLayout coordinatorLayout;
    Intent intent;
    String[] Titles = {"Featured", "Favourite", "your"};
    int Numboftabs = Titles.length;
    Context c = null;
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
        setContentView(R.layout.activity_recipes);

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


        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(R.id.Recipes);
        View header = navigationView.getHeaderView(0);


        tvUsername = (TextView) header.findViewById(R.id.tvUsernameDrawer);
        tvUsername.setText(g.getUsername());
        ImageView imgProfile = (ImageView) header.findViewById(R.id.imgProfile);
        imgProfile.setImageBitmap(getBitmapFromURL(g.getFotoUrl()));

        fabAddNewRecipe = (FloatingActionButton) findViewById(R.id.fabAddNewRecipe);
        fabAddNewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewRecipe();
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
                        ShopListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(ShopListIntent);
                        finish();
                        return true;
                    case R.id.Recipes:
                        Intent RecipeIntent = new Intent(getBaseContext(), RecipesActivity.class);
                        RecipeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
                                        navigationView.setCheckedItem(R.id.Recipes);
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

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);

        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    private void CreateNewRecipe() {
        Intent myIntent = new Intent(RecipesActivity.this, CreateRecipeActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myIntent);
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
        //    Intent intent = new Intent(RecipesActivity.this, SettingsActivity.class);
        //    startActivity(intent);
        //}

        return super.onOptionsItemSelected(item);
    }


}

