package com.crowderia.mytoz.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.crowderia.mytoz.R;
import com.crowderia.mytoz.adapter.CategoryListAdapter;
import com.crowderia.mytoz.db.DBHelper;
import com.crowderia.mytoz.model.CategoryModel;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    ListView categoryList;
    ArrayList<CategoryModel> categoryModelsList;
    private static CategoryListAdapter categoryListAdapter;
    // Database object reference
    DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_interest, container, false);

        categoryList = (ListView)view.findViewById(R.id.interestListView);

        // Retrieve available categories
        getCategoryList();

        // Return the layout
        return view;
    }

    // Get categories
    private void getCategoryList() {

        // Create new category list object
        categoryModelsList = new ArrayList<>();

        // Create db instance
        dbHelper = new DBHelper(getActivity());

        try {
            // Get available category count from database table
            int categoryRowCount = dbHelper.numberOfCategoryRows();

            // Retrieve all categories from database to Cursor
            Cursor cursor = dbHelper.getAllCategories();

            // Check the cursor has data (category count)
            if (cursor != null && cursor.getCount() > 0) {

                Log.i("Category count",cursor.getCount()+"");

                while (cursor.moveToNext()) {

                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String isSelected = cursor.getString(cursor.getColumnIndex("is_selected"));

                    Log.i("Categories", name + " " + isSelected);

                    CategoryModel categoryList = new CategoryModel(name, isSelected);
                    categoryModelsList.add(categoryList);
                }
                // Close the cursor
                cursor.close();
            } else {
                // If there's no categories in the database
                // Add new categories to database as interested
                dbHelper.insertCategory("ARTS_ENTERTAINMENT", "1");
                dbHelper.insertCategory("AUIOS_VEHICLES", "1");
                dbHelper.insertCategory("BEAUTY_FITNESS", "1");
                dbHelper.insertCategory("COMPUTERS_ELECTRONICS", "1");
                dbHelper.insertCategory("FOOD_DRINKS", "1");
                dbHelper.insertCategory("GAMES", "1");

                // Get categories to cursor
                Cursor cursor2 = dbHelper.getAllCategories();

                while (cursor2.moveToNext()) {

                    String name = cursor2.getString(cursor2.getColumnIndex("name"));
                    String isSelected = cursor2.getString(cursor2.getColumnIndex("is_selected"));

                    Log.i("Categories", name + " " + isSelected);

                    // Create new category model
                    CategoryModel categoryList = new CategoryModel(name, isSelected);

                    // Add category model to list
                    categoryModelsList.add(categoryList);
                }
                // Close the cursor
                cursor2.close();
            }

            // Create new list adapter
            // add category list to adapter
            categoryListAdapter = new CategoryListAdapter(getActivity(), categoryModelsList);

            // Bind adapter with the list
            categoryList.setAdapter(categoryListAdapter);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
