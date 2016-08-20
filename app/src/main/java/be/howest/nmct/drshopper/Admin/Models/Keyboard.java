package be.howest.nmct.drshopper.Admin.Models;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Nicolas on 22-Dec-15.
 */
public class Keyboard {

    public static void toggle(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive()){
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        } else {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
        }
    }//end method
}

