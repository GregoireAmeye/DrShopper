package be.howest.nmct.shopperio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutionException;

import be.howest.nmct.shopperio.Admin.Globals;
import be.howest.nmct.shopperio.Admin.Models.Ingredient;
import be.howest.nmct.shopperio.Admin.Models.Quantity;
import be.howest.nmct.shopperio.Admin.Models.ShoppingList;
import be.howest.nmct.shopperio.Service.ShoppingListService;

////
public class ShoppingListDetailActivity extends AppCompatActivity {

    public final static String EXTRA_SL = "be.howest.nmct.drshopper.SHOPPINGLIST";
    ShoppingList sl = null;
    List<Quantity> quantities = null;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_detaill);

        Globals g = Globals.getInstance();
        String token = g.getToken();

        Intent intent = getIntent();
        int id = intent.getIntExtra(EXTRA_SL, 0);
        try {
            sl = new ShoppingListService.getByIdAsync().execute("" + id).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        TextView tvNameList = (TextView) findViewById(R.id.tvNameList);
        tvNameList.setText(sl.getName());

        final ListView lstIngredients = (ListView) findViewById(R.id.lstIngredients);
        String sIngredients = "";
        for (Ingredient i : sl.getIngredients()) {
            sIngredients += i.getName() + "-";
        }

        String[] values = sIngredients.split("-");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        lstIngredients.setAdapter(adapter);

        lstIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) lstIngredients.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        ImageView imgList = (ImageView) findViewById(R.id.imgList);
        Picasso.with(getBaseContext()).load(sl.getUrl()).into(imgList);


        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new ShoppingListService.addIngredientToList().execute();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shoplistdetail, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println(item.getItemId());
        switch (item.getItemId()) {
            case R.id.action_delete:
                promptDialog();
                break;
            case 16908332:
                onBackPressed();
            default:
                break;

        }
        return true;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingListDetailActivity.this);
        builder.setMessage("Do you really want to delete " + sl.getName() + "?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

    private void deleteThisList() {
        ShoppingListService.deleteShoppingList(sl.getId());
        Intent intent = new Intent(ShoppingListDetailActivity.this, ShoppingListsActivity.class);
        startActivity(intent);
    }
}
