package be.howest.nmct.shopperio;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import be.howest.nmct.shopperio.Admin.Models.ShoppingList;

//
public class ListAdapter extends ArrayAdapter<ShoppingList> {

    public static List<ShoppingList> ShoppingLists;
    public SparseBooleanArray mSelectedItemsIds;
    ShoppingList sl;
    private LayoutInflater inflater;
    private Context mContext;

    public ListAdapter(Context context, List<ShoppingList> ShoppingLists) {
        super(context, R.layout.row_list, R.id.tvNameList, ShoppingLists);
        mSelectedItemsIds = new SparseBooleanArray();
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.ShoppingLists = ShoppingLists;
    }

    public void clearAdapter() {
        ShoppingLists.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View row = super.getView(position, convertView, parent);
        sl = ShoppingLists.get(position);
        ViewHolder vh = (ViewHolder) row.getTag();

        if (vh == null) {
            vh = new ViewHolder(row);
            row.setTag(vh);
        }

        TextView tvNameRecipe = vh.tvNameList;
        tvNameRecipe.setText(sl.getName());


        final ImageView imgList = (ImageView) vh.imgList;
        if (sl.getUrl() != null)
            Picasso.with(getContext()).load(sl.getUrl()).into(imgList);
        else
            Picasso.with(getContext()).load(R.drawable.list2).into(imgList);


        return row;


    }

    public void remove(String string) {
        ShoppingLists.remove(string);
        notifyDataSetChanged();
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    class ViewHolder {

        TextView tvNameList;
        ImageView imgList;

        public ViewHolder(View row) {
            tvNameList = (TextView) row.findViewById(R.id.tvNameList);
            imgList = (ImageView) row.findViewById(R.id.imgList);
        }
    }
}
