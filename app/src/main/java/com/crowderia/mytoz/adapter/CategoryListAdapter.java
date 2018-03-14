package com.crowderia.mytoz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.crowderia.mytoz.R;
import com.crowderia.mytoz.db.DBHelper;
import com.crowderia.mytoz.model.CategoryModel;

import java.util.ArrayList;

/**
 * Created by crowderia on 11/1/16.
 */

public class CategoryListAdapter extends ArrayAdapter<CategoryModel> {

    private final Context context;
    private ArrayList<CategoryModel> interestArrayList;
    ViewHolder viewHolder;



    public CategoryListAdapter(Context context, ArrayList<CategoryModel> interestArrayList) {
        super(context, R.layout.interest_list_item, interestArrayList);
        this.context = context;
        this.interestArrayList = interestArrayList;
    }

    private static class ViewHolder {
        TextView textView;
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final CategoryModel interestModel = getItem(position);

       final View result;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.interest_list_item, parent, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.interest_name);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.interest_check);
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view.findViewById(R.id.interest_check);
                    String catName = interestModel.getCategoryName();
                    if(cb.isChecked()){
                        changeCategoryStatus(catName, "1");
                    } else {
                        changeCategoryStatus(catName, "0");
                    }
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String categoryName = "";
        switch (interestModel.getCategoryName()) {
            case "ARTS_ENTERTAINMENT":
                categoryName = context.getString(R.string.category_arts);
                break;
            case "AUIOS_VEHICLES":
                categoryName = context.getString(R.string.category_autos);
                break;
            case "BEAUTY_FITNESS":
                categoryName = context.getString(R.string.category_beauty);
                break;
            case "COMPUTERS_ELECTRONICS":
                categoryName = context.getString(R.string.category_computers);
                break;
            case "FOOD_DRINKS":
                categoryName = context.getString(R.string.category_food);
                break;
            case "GAMES":
                categoryName = context.getString(R.string.category_games);
                break;
        }

        viewHolder.textView.setText(categoryName);
        viewHolder.checkBox.setChecked(interestModel.isInterested().equalsIgnoreCase("1")?true:false);

        return convertView;
    }

    private void changeCategoryStatus(String name, String status){

        // create db instance
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.updateCategorySelectedStatus(name, status);

    }

}
