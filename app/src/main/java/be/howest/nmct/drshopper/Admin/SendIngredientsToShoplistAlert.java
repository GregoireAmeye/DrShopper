package be.howest.nmct.drshopper.Admin;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import be.howest.nmct.drshopper.Admin.Models.ShoppingList;
import be.howest.nmct.drshopper.ListAdapter;
import be.howest.nmct.drshopper.R;
import be.howest.nmct.drshopper.Service.ShoppingListService;
import be.howest.nmct.drshopper.ShoppingListDetailActivity;
import be.howest.nmct.drshopper.ShoppingListDetailScrollActivity;
import be.howest.nmct.drshopper.ShoppingListsActivity;

public class SendIngredientsToShoplistAlert extends DialogFragment {
    public onSendListener mListener;
    ListView lst = null;
    ListAdapter mAdapter = null;
    private List<ShoppingList> ShoppingLists = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.popup_sendingrtoshoplist);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.WHITE));
        dialog.show();

        lst = (ListView) dialog.findViewById(R.id.lstIngredientsForPopup);


        try {
            if(ShoppingLists!=null) ShoppingLists.clear();
            ShoppingLists = new ShoppingListService.getListsAsync().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {

            mAdapter = new ListAdapter(getActivity(), ShoppingLists);

            lst.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            lst.setAdapter(null);
        }



        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.sendIngredientsToShoppinglist(ShoppingLists.get(position).getId(), ShoppingLists.get(position).getName());
                dismiss();
            }
        });



        return dialog;
    }

    public interface onSendListener{
        void sendIngredientsToShoppinglist(int shoppinglistId, String shoppinglistName);
    }
}
