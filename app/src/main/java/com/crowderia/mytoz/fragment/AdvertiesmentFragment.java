package com.crowderia.mytoz.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.crowderia.mytoz.R;
import com.crowderia.mytoz.activity.AdOpenBrowserActivity;
import com.crowderia.mytoz.activity.LockScreen;
import com.crowderia.mytoz.adapter.AdvertiesmentListAdapter;
import com.crowderia.mytoz.db.DBHelper;
import com.crowderia.mytoz.model.CampaignModel;
import com.crowderia.mytoz.util.MytozConstant;
import com.crowderia.mytoz.util.Preference;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/*
*
**/
public class AdvertiesmentFragment extends Fragment {

    ListView adList;

    // CampaignModel array list
    ArrayList<CampaignModel> campaignModelsList;

    //Adapter for bind response values to list
    public static AdvertiesmentListAdapter adListAdapter;

    DBHelper dbHelper;

    private Context context = getActivity();
    private Intent browsIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        adList = (ListView)view.findViewById(R.id.adListView);

        // API request for get campaigns what I interested
        getCampaigns();

        // Return the final layout
        return view;
    }

    private void getCampaigns(){
        // Create new array list for admodel
        campaignModelsList = new ArrayList<>();

        // Create db instance
        dbHelper = new DBHelper(getActivity());

        try {
            // Get the interested campaign count
            int campaignRowCount = dbHelper.numberOfCampaignRows();
            Log.e("Campaign row count",campaignRowCount+"");

            // Retrieve interested campaigns from database
            Cursor cursor = dbHelper.getAllCampaignsFromInterest();

            // Check the availability of interested campaigns
            if (cursor != null && cursor.getCount() > 0) {
                Log.e("DATA",cursor.getCount()+"");

                while (cursor.moveToNext()) {
                    String campaignId = cursor.getString(cursor.getColumnIndex("campaign_id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String category = cursor.getString(cursor.getColumnIndex("category"));
                    String appImageUrl = (cursor.getString(cursor.getColumnIndex("app_image_url"))==null?"":cursor.getString(cursor.getColumnIndex("app_image_url")));
                    String lockImageUrl = (cursor.getString(cursor.getColumnIndex("lock_image_url"))==null?"":cursor.getString(cursor.getColumnIndex("lock_image_url")));
                    String webUrl = (cursor.getString(cursor.getColumnIndex("web_url"))==null?"":cursor.getString(cursor.getColumnIndex("web_url")));

                    Log.i("Campaigns from DB >>", campaignId + " "
                            + appImageUrl + " "
                            + lockImageUrl + " "
                            + name + " "
                            + category + " "
                            + webUrl);

                    // Create new campaign model
                    CampaignModel campaignList = new CampaignModel(campaignId, appImageUrl, lockImageUrl, name, category, webUrl);
                    // Add campaign to campaign model list
                    campaignModelsList.add(campaignList);
                }
                cursor.close();
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // Create new adapter instance for bine items to list
        adListAdapter = new AdvertiesmentListAdapter(getActivity(), campaignModelsList);

        // Adapter bind to the list
        adList.setAdapter(adListAdapter);

        // List item click
        adList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("position >>", position + "");
                // Get clicked list item model from list
                CampaignModel dataModel= campaignModelsList.get(position);

                // Create new intent for open browser
                browsIntent = new Intent(getActivity(), AdOpenBrowserActivity.class);

                browsIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // Parse web url with the intent
                browsIntent.putExtra("url", dataModel.getCampaignWebUrl().toString());
                // Start browser activity
                startActivity(browsIntent);
            }
        });

    }
}