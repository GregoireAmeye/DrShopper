package be.howest.nmct.drshopper.Admin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import be.howest.nmct.drshopper.Admin.Models.OfflineShoppingListsContract;
import be.howest.nmct.drshopper.Admin.Models.Recipe;
import be.howest.nmct.drshopper.Admin.Models.ShoppingList;
import be.howest.nmct.drshopper.R;
import be.howest.nmct.drshopper.Service.RecipeService;

public class OfflineShoppinglistDatabase {

    private ShoppingListHelper helper;
    private SQLiteDatabase db;
    private final Context context;

    public OfflineShoppinglistDatabase(Context context) {
        this.context = context;
    }

    public OfflineShoppinglistDatabase open() {
        helper = new ShoppingListHelper(this.context);
        db = helper.getWritableDatabase();
        return this;
    }

    public void close() {
        helper.close();
    }




    public List<ShoppingList> getAllShoppinglists(){
        List<ShoppingList> shoppingLists = new ArrayList<>();
        String[] projection = {
                OfflineShoppingListsContract.ShoppingListEntry._ID,
                OfflineShoppingListsContract.ShoppingListEntry.COLUMN_NAME_SHOPPINGLISTID,
                OfflineShoppingListsContract.ShoppingListEntry.COLUMN_NAME_SHOPPINGLISTNAME
        };

        Cursor c = db.query(
                OfflineShoppingListsContract.ShoppingListEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        while(c.moveToNext()){
            ShoppingList l = new ShoppingList();
            String shoppinglistid =  c.getString(c.getColumnIndexOrThrow(OfflineShoppingListsContract.ShoppingListEntry.COLUMN_NAME_SHOPPINGLISTID));
            String shoppinglistname = c.getString(c.getColumnIndexOrThrow(OfflineShoppingListsContract.ShoppingListEntry.COLUMN_NAME_SHOPPINGLISTNAME));
            int id = Integer.parseInt(shoppinglistid);

            l.setId(id);
            l.setNaam(shoppinglistname);
            shoppingLists.add(l);

        }
        c.close();


        return shoppingLists;
    }

    public void saveAllShoppinglists(List<ShoppingList> shoppingLists){
        db.execSQL("delete from " + OfflineShoppingListsContract.ShoppingListEntry.TABLE_NAME);



        for(ShoppingList sl : shoppingLists){

            ContentValues values = new ContentValues();
            values.put(OfflineShoppingListsContract.ShoppingListEntry.COLUMN_NAME_SHOPPINGLISTID, sl.getId());
            values.put(OfflineShoppingListsContract.ShoppingListEntry.COLUMN_NAME_SHOPPINGLISTNAME, sl.getName());


            db.insert(
                    OfflineShoppingListsContract.ShoppingListEntry.TABLE_NAME,
                    null,
                    values);
        }

    }

    private static class ShoppingListHelper extends SQLiteOpenHelper{
        public static final int DATABASE_VERSION = 4;
        public static final String DATABASE_NAME = "ShoppingListReader.db";

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + OfflineShoppingListsContract.ShoppingListEntry.TABLE_NAME + " (" +
                        OfflineShoppingListsContract.ShoppingListEntry._ID + " INTEGER PRIMARY KEY," +
                        OfflineShoppingListsContract.ShoppingListEntry.COLUMN_NAME_SHOPPINGLISTID + TEXT_TYPE + COMMA_SEP +
                        OfflineShoppingListsContract.ShoppingListEntry.COLUMN_NAME_SHOPPINGLISTNAME + TEXT_TYPE +

                " )";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + OfflineShoppingListsContract.ShoppingListEntry.TABLE_NAME;

        public ShoppingListHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
