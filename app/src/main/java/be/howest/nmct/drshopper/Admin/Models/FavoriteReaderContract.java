package be.howest.nmct.drshopper.Admin.Models;

import android.provider.BaseColumns;

/**
 * Created by Nicolas on 05-Jan-16.
 */
public class FavoriteReaderContract {

    public FavoriteReaderContract() {
    }

    public static abstract class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_NAME_SHOPPERRECIPEID = "shopperRecipeId";

    }
}
