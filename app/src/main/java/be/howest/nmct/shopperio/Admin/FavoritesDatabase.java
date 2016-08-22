package be.howest.nmct.shopperio.Admin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import be.howest.nmct.shopperio.Admin.Models.FavoriteReaderContract;
import be.howest.nmct.shopperio.Admin.Models.Recipe;
import be.howest.nmct.shopperio.Service.RecipeService;
import be.howest.nmct.shopperio.Service.YummlyService;

public class FavoritesDatabase {

    private FavoriteRecipesHelper helper;
    private SQLiteDatabase db;
    private final Context context;

    public FavoritesDatabase(Context context) {
        this.context = context;
    }

    public FavoritesDatabase open() {
        helper = new FavoriteRecipesHelper(this.context);
        db = helper.getWritableDatabase();
        return this;
    }

    public void close() {
        helper.close();
    }



    public List<Recipe> readAllFavorites(){

        List<Recipe> recipes = new ArrayList<>();



        String[] projection = {
                FavoriteReaderContract.FavoriteEntry._ID,
                FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_SHOPPERRECIPEID
        };

        Cursor c = db.query(
                FavoriteReaderContract.FavoriteEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        while(c.moveToNext()){
            Recipe r = null;
            String recipeid =  c.getString(c.getColumnIndexOrThrow(FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_SHOPPERRECIPEID));
            try {
                if(tryParseInt(recipeid)){
                    r = new RecipeService.getByIdAsync().execute(recipeid).get();
                }
                else{
                    recipeid = recipeid.replace(" ","%20");
                    recipeid = recipeid.replace("'", "%27");
                    r = YummlyService.SearchRecipes(recipeid).get(0);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (Exception e)
            {

            }
            if(r!=null){
                recipes.add(r);
            }
        }


        return recipes;
    }

    public void addFavorite(String id){

        // Gets the data repository in write mode


        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_SHOPPERRECIPEID, id);


        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                FavoriteReaderContract.FavoriteEntry.TABLE_NAME,
                null,
                values);
    }

    public void deleteFavorite(String id){

        // Define 'where' part of query.
        String selection = FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_SHOPPERRECIPEID + " LIKE ?";

        String[] selectionArgs = { String.valueOf(id) };

        db.delete(FavoriteReaderContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
    }
    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }



    private static class FavoriteRecipesHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FavoriteReader.db";

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + FavoriteReaderContract.FavoriteEntry.TABLE_NAME + " (" +
                        FavoriteReaderContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY," +
                        FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_SHOPPERRECIPEID + TEXT_TYPE +

                        " )";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + FavoriteReaderContract.FavoriteEntry.TABLE_NAME;

        public FavoriteRecipesHelper(Context context){
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

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }





    }

}
