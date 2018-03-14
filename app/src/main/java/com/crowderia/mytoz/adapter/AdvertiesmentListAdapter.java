package com.crowderia.mytoz.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.crowderia.mytoz.R;
import com.crowderia.mytoz.activity.AdOpenBrowserActivity;
import com.crowderia.mytoz.db.DBHelper;
import com.crowderia.mytoz.fragment.AdvertiesmentFragment;
import com.crowderia.mytoz.model.CampaignModel;
import com.crowderia.mytoz.util.PicassoSingleton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by crowderia on 11/1/16.
 */

public class AdvertiesmentListAdapter extends ArrayAdapter<CampaignModel> {

    private final Context context;
    private Intent browsIntent;
    private ArrayList<CampaignModel> adArrayList;

    /**
     * Create AdvertiesmentListAdapter constructor.
     *
     * @param context
     * @param adArrayList
     */
    public AdvertiesmentListAdapter(Context context, ArrayList<CampaignModel> adArrayList) {
        super(context, R.layout.advertiesment_list_item, adArrayList);
        this.context = context;
        this.adArrayList = adArrayList;
    }

    private static class ViewHolder {
        ImageView imageView;
        ImageButton removeCampaign;
        TextView textViewDescription;
        TextView textViewCategory;
    }

    /**
     * Gets the view of list item.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return View.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final CampaignModel campaignModel = getItem(position);

        ViewHolder viewHolder;
        final View result;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.advertiesment_list_item, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.ivAd);
            viewHolder.textViewDescription = (TextView)convertView.findViewById(R.id.description);
            viewHolder.textViewCategory = (TextView)convertView.findViewById(R.id.category);
            viewHolder.removeCampaign = (ImageButton)convertView.findViewById(R.id.ibRemoveCampaign);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        String imageUrl = campaignModel.getImageUrl().replace("[", "").replace("]", "").replace("\"", "");
        Log.e("Image Url", imageUrl);

        final int radius = 10;
        final int margin = 10;

        PicassoSingleton.getInstance(context).load(imageUrl.toString())
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(viewHolder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.e("Picasso", "onSuccess");
            }

            @Override
            public void onError() {
                Log.e("Picasso", "onError");
            }
        });

        String categoryName = "";
        switch (campaignModel.getCategory()) {
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
        viewHolder.textViewDescription.setText(campaignModel.getCampaignName());
        viewHolder.textViewCategory.setText(categoryName);

        viewHolder.removeCampaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (campaignModel.getCampaignId()!=null) {
                    try {
                        boolean isDeleted = deleteCampaignFromInterest(campaignModel.getCampaignId());
                        if (isDeleted) {
                            AdvertiesmentFragment.adListAdapter.remove(AdvertiesmentFragment.adListAdapter.getItem(position));
                            AdvertiesmentFragment.adListAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create new intent for open browser
                browsIntent = new Intent(context, AdOpenBrowserActivity.class);
                browsIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // Parse web url with the intent
                browsIntent.putExtra("url", campaignModel.getCampaignWebUrl().toString());
                // Start browser activity
                context.startActivity(browsIntent);

            }
        });

        return convertView;
    }

    private boolean deleteCampaignFromInterest(String campaignId){

        // create db instance
        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.deleteCampaignById(campaignId);
    }
}
