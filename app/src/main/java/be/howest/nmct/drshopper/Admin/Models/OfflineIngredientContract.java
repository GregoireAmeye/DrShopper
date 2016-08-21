package be.howest.nmct.drshopper.Admin.Models;

import android.provider.BaseColumns;

/**
 * Created by Nicolas on 05-Jan-16.
 */
public class OfflineIngredientContract {

    public static abstract class OfflineIngredientEntry implements BaseColumns {
        public static final String TABLE_NAME = "offlineingredients";
        public static final String COLUMN_NAME_INGREDIENTID = "ingrID";
        public static final String COLUMN_NAME_INGREDIENTNAME = "ingrName";
        public static final String COLUMN_NAME_INGREDIENTQUANTITYVAL = "ingrQuanVal";
        public static final String COLUMN_NAME_INGREDIENTQUANTITYNAME = "ingrQuanName";
        public static final String COLUMN_NAME_SHOPPINGLISTID = "shoplistId";
    }
}
