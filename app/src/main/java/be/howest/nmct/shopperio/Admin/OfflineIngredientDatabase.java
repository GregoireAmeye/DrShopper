package be.howest.nmct.shopperio.Admin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import be.howest.nmct.shopperio.Admin.Models.Ingredient;
import be.howest.nmct.shopperio.Admin.Models.OfflineIngredientContract;

public class OfflineIngredientDatabase {

    private IngredientHelper helper;
    private SQLiteDatabase db;
    private final Context context;

    public OfflineIngredientDatabase(Context context) {
        this.context = context;
    }

    public OfflineIngredientDatabase open() {
        helper = new IngredientHelper(this.context);
        db = helper.getWritableDatabase();
        return this;
    }

    public void close() {
        helper.close();
    }

    public List<Ingredient> getAllIngredientsFromShoppinglist(int shoplistid) {

        List<Ingredient> ingredients = new ArrayList<>();

        String[] projection = {
                OfflineIngredientContract.OfflineIngredientEntry._ID,
                OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTID,
                OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTNAME,
                OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTQUANTITYVAL,
                OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTQUANTITYNAME,
                OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_SHOPPINGLISTID

        };


        Cursor c = db.query(
                OfflineIngredientContract.OfflineIngredientEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_SHOPPINGLISTID + " = ?",                                // The columns for the WHERE clause
                new String[]{shoplistid + ""},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );


        while (c.moveToNext()) {
            Ingredient i = null;

            String ingredientId = c.getString(c.getColumnIndexOrThrow(OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTID));
            String ingredientName = c.getString(c.getColumnIndexOrThrow(OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTNAME));
            String quantityName = c.getString(c.getColumnIndexOrThrow(OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTQUANTITYNAME));
            String quantityValue = c.getString(c.getColumnIndexOrThrow(OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTQUANTITYVAL));
            int id = Integer.parseInt(ingredientId);


            i = new Ingredient(id,ingredientName,quantityName,quantityValue);

            ingredients.add(i);

        }
        c.close();

        return ingredients;
    }

    public void cacheIngredients(List<Ingredient> ingredients, int shoplistId){
        String whereClause = OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_SHOPPINGLISTID + "=?";
        String[] whereArgs = new String[] { String.valueOf(shoplistId) };
        db.delete(OfflineIngredientContract.OfflineIngredientEntry.TABLE_NAME, whereClause, whereArgs);

        for(Ingredient i : ingredients){
            ContentValues values = new ContentValues();

            values.put(OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTID, i.getID());
            values.put(OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTNAME, i.getName());
            values.put(OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTQUANTITYNAME, i.getQuantityName());
            values.put(OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTQUANTITYVAL, i.getQuantity());
            values.put(OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_SHOPPINGLISTID, shoplistId);


            db.insert(
                    OfflineIngredientContract.OfflineIngredientEntry.TABLE_NAME,
                    null,
                    values);
        }




    }




    private static class IngredientHelper extends SQLiteOpenHelper{
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "IngredientReader.db";

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + OfflineIngredientContract.OfflineIngredientEntry.TABLE_NAME + " (" +
                        OfflineIngredientContract.OfflineIngredientEntry._ID + " INTEGER PRIMARY KEY," +
                        OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTID + TEXT_TYPE + COMMA_SEP +
                        OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTNAME + TEXT_TYPE + COMMA_SEP +
                        OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTQUANTITYVAL + TEXT_TYPE + COMMA_SEP +
                        OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_INGREDIENTQUANTITYNAME + TEXT_TYPE + COMMA_SEP +
                        OfflineIngredientContract.OfflineIngredientEntry.COLUMN_NAME_SHOPPINGLISTID + TEXT_TYPE +

                        " )";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + OfflineIngredientContract.OfflineIngredientEntry.TABLE_NAME;

        public IngredientHelper(Context context){
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
