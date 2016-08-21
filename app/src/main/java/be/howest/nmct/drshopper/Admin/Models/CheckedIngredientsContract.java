package be.howest.nmct.drshopper.Admin.Models;

import android.provider.BaseColumns;

/**
 * Created by Nicolas on 23-Dec-15.
 */
public final class CheckedIngredientsContract {

    public CheckedIngredientsContract() {
    }


    public static abstract class IngredientEntry implements BaseColumns {
        public static final String TABLE_NAME = "ingredient";
        public static final String COLUMN_NAME_INGREDIENT_ID = "ingredientId";
        public static final String COLUMN_NAME_CHECKED = "CHECKED";
        public static final String COLUMN_NAME_SHOPPINGLIST_ID = "shoppinglistId";


    }

}
