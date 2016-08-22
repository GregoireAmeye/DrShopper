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
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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

public class ProfileActivity extends AppCompatActivity {


    TextView tvUsername;
    TextView tvName;
    Toolbar toolbar;
    Intent intent;
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
        setContentView(R.layout.activity_profile);

        //using singleton
        Globals g = Globals.getInstance();
        String email = g.getEmail();

        // Initializing Drawer Layout and ActionBarToggle
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

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



        //profile
        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(g.getUsername());







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
                                        //navigationView.setCheckedItem(R.id.Account);
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
}
