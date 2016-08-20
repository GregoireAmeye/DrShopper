package be.howest.nmct.drshopper.Admin.Models;

import android.provider.BaseColumns;

/**
 * Created by Nicolas on 05-Jan-16.
 */
public class OfflineShoppingListsContract {
    public OfflineShoppingListsContract(){}

    public static abstract class ShoppingListEntry implements BaseColumns{
        public static final String TABLE_NAME = "shoppinglists";
        public static final String COLUMN_NAME_SHOPPINGLISTID = "shoppinglistId";
        public static final String COLUMN_NAME_SHOPPINGLISTNAME = "shoppinglistName";
    }
}
