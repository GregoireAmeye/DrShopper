package be.howest.nmct.drshopper.Admin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import be.howest.nmct.drshopper.Admin.Models.CheckedIngredientsContract;

public class CheckIngredientsHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CheckedIngredientsContract.IngredientEntry.TABLE_NAME + " (" +
                    CheckedIngredientsContract.IngredientEntry._ID + " INTEGER PRIMARY KEY," +
                    CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_INGREDIENT_ID + TEXT_TYPE + COMMA_SEP +
                    CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_CHECKED + TEXT_TYPE + COMMA_SEP +
                    CheckedIngredientsContract.IngredientEntry.COLUMN_NAME_SHOPPINGLIST_ID + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CheckedIngredientsContract.IngredientEntry.TABLE_NAME;
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "FeedReader.db";

    public CheckIngredientsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
