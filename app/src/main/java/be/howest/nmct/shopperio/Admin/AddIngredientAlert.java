package be.howest.nmct.shopperio.Admin;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import be.howest.nmct.shopperio.R;

public class AddIngredientAlert extends DialogFragment {
    public OnAddListener mListener;
    EditText etName, etQuanitity;
    AutoCompleteTextView etMeasure;
    Button addIngredient;
    public static final String[] MEASURES = new String[]{
            "cup", "ml", "kg", "oz","g","l","pint","pound"
    };


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.popup_add_ingredient);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.WHITE));
        dialog.show();

        addIngredient = (Button) dialog.findViewById(R.id.btnAddIngredient);
        addIngredient.setEnabled(false);
        etName = (EditText) dialog.findViewById(R.id.etIngredientName);

        etName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mListener.addNewIngredient(etName.getText().toString(),etQuanitity.getText().toString(), etMeasure.getText().toString());
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        etName.setError("Required");
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(etName.getText().toString().length()<=0){
                    etName.setError("Required");
                    addIngredient.setEnabled(false);

                }
                else{
                    etName.setError(null);
                    addIngredient.setEnabled(true);
                }

            }
        });
        etQuanitity = (EditText) dialog.findViewById(R.id.etQuantity);
        etMeasure = (AutoCompleteTextView) dialog.findViewById(R.id.etMeasure);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, MEASURES);
        etMeasure.setThreshold(0);
        etMeasure.setAdapter(adapter);

        etName.setFocusable(true);


        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.addNewIngredient(etName.getText().toString(),etQuanitity.getText().toString(), etMeasure.getText().toString());
                dismiss();
            }
        });

        return dialog;
    }



    public interface OnAddListener{
        void addNewIngredient(String ingredientName, String quanitity, String measure);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  super.onCreateView(inflater, container, savedInstanceState);


        etName.requestFocus();

        return v;
    }
}
