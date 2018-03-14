package com.crowderia.mytoz.db;

/**
 * Created by Crowderia on 11/10/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.crowderia.mytoz.model.ProfileModel;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mytoz.db";

    public static final String TABLE_CAMPAIGN_INTEREST= "interest_campaign";
    public static final String CAMPAIGN_COLUMN_ID = "campaign_id";
    public static final String CAMPAIGN_COLUMN_NAME = "name";
    public static final String CAMPAIGN_COLUMN_CATEGORY = "category";
    public static final String CAMPAIGN_COLUMN_APP_IMAGE_URL = "app_image_url";
    public static final String CAMPAIGN_COLUMN_LOCK_IMAGE_URL = "lock_image_url";
    public static final String CAMPAIGN_COLUMN_DATE_ADDED = "date_interested";
    public static final String CAMPAIGN_COLUMN_WEB_URL = "web_url";

    public static final String TABLE_PROFILE = "profile";
    public static final String PROFILE_COLUMN_ID = "profile_id";
    public static final String PROFILE_COLUMN_NAME = "name";
    public static final String PROFILE_COLUMN_PHONE = "phone";
    public static final String PROFILE_COLUMN_EMAIL = "email";
    public static final String PROFILE_COLUMN_DOB = "dob";
    public static final String PROFILE_COLUMN_SEX = "sex";

    public static final String TABLE_CATEGORY = "category";
    public static final String CATEGORY_COLUMN_NAME = "name";
    public static final String CATEGORY_COLUMN_IS_SELECTED= "is_selected";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(
                "create table if not exists " + TABLE_CAMPAIGN_INTEREST +
                        "("+ CAMPAIGN_COLUMN_ID + " text primary key, "
                        + CAMPAIGN_COLUMN_NAME + " text, "
                        + CAMPAIGN_COLUMN_CATEGORY + " text, "
                        + CAMPAIGN_COLUMN_APP_IMAGE_URL + " text, "
                        + CAMPAIGN_COLUMN_LOCK_IMAGE_URL+ " text, "
                        + CAMPAIGN_COLUMN_DATE_ADDED + " text, "
                        + CAMPAIGN_COLUMN_WEB_URL + " text)");

        sqLiteDatabase.execSQL(
                "create table if not exists " + TABLE_PROFILE +
                        "(" + PROFILE_COLUMN_ID + " integer primary key autoincrement, "
                        + PROFILE_COLUMN_NAME + " text, "
                        + PROFILE_COLUMN_PHONE + " text unique, "
                        + PROFILE_COLUMN_EMAIL + " text, "
                        + PROFILE_COLUMN_DOB + " text, "
                        + PROFILE_COLUMN_SEX + " text)");

        sqLiteDatabase.execSQL(
                "create table if not exists " + TABLE_CATEGORY +
                        "(" + CATEGORY_COLUMN_NAME + " text primary key unique, "
                        + CATEGORY_COLUMN_IS_SELECTED + " text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CAMPAIGN_INTEREST);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(sqLiteDatabase);
    }

    public boolean insertInterestCampaign(String campaign_id, String name, String category, String imageUrl, String imageUrlLock, String insertDate, String webUrl)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CAMPAIGN_COLUMN_ID, campaign_id);
        contentValues.put(CAMPAIGN_COLUMN_NAME, name);
        contentValues.put(CAMPAIGN_COLUMN_CATEGORY, category);
        contentValues.put(CAMPAIGN_COLUMN_APP_IMAGE_URL, imageUrl);
        contentValues.put(CAMPAIGN_COLUMN_LOCK_IMAGE_URL, imageUrlLock);
        contentValues.put(CAMPAIGN_COLUMN_DATE_ADDED, insertDate);
        contentValues.put(CAMPAIGN_COLUMN_WEB_URL, webUrl);
        db.insert(TABLE_CAMPAIGN_INTEREST, null, contentValues);
        return true;
    }

    public boolean insertCategory(String name, String isSelected)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_COLUMN_NAME, name);
        contentValues.put(CATEGORY_COLUMN_IS_SELECTED, isSelected);

        db.insert(TABLE_CATEGORY, null, contentValues);
        return true;
    }

    public int insertProfileInfo(ProfileModel profileBean)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor query = db.query(TABLE_PROFILE, null, null, null, null, null, null);
        contentValues.put(PROFILE_COLUMN_NAME, profileBean.getName());
        contentValues.put(PROFILE_COLUMN_PHONE, profileBean.getPhone());
        contentValues.put(PROFILE_COLUMN_EMAIL, profileBean.getEmail());
        contentValues.put(PROFILE_COLUMN_DOB, profileBean.getDob());
        contentValues.put(PROFILE_COLUMN_SEX, profileBean.getSex());

        if (query.getCount() < 1) {
            db.insert(TABLE_PROFILE, null, contentValues);
            return 1;

        } else if (query.getCount() == 1) {
            db.update(TABLE_PROFILE, contentValues, null, null);
            return 2;
        }
        return 0;
    }

    public int numberOfCampaignRows()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_CAMPAIGN_INTEREST);
        return numRows;
    }

    public int numberOfCategoryRows()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_CATEGORY);
        return numRows;
    }

    public int getCampaignById(String campaignId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_CAMPAIGN_INTEREST + " where campaign_id=\""+campaignId+"\"", null );

        if(res.getCount()==1){
            res.close();
            return 1;
        }

        return 0;
    }

    public boolean deleteCampaignById(String campaignId){
        boolean isDeleted;
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "delete from " + TABLE_CAMPAIGN_INTEREST + " where campaign_id=\""+campaignId+"\"";
        db.execSQL( deleteQuery );
        isDeleted = true;

        return isDeleted;
    }

    public Cursor getAllCampaignsFromInterest()
    {
        SQLiteDatabase db;
        Cursor res = null;
        try {
            db = this.getReadableDatabase();

            String query = "SELECT " + CAMPAIGN_COLUMN_ID + "," + CAMPAIGN_COLUMN_NAME + "," + CAMPAIGN_COLUMN_CATEGORY + "," + CAMPAIGN_COLUMN_APP_IMAGE_URL + "," + CAMPAIGN_COLUMN_LOCK_IMAGE_URL + "," + CAMPAIGN_COLUMN_WEB_URL + " FROM " + TABLE_CAMPAIGN_INTEREST ;
            res = db.rawQuery(query,null);
            if (res != null) {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public Cursor getAllCategories()
    {
        SQLiteDatabase db;
        Cursor res = null;
        try {
            db = this.getReadableDatabase();

            String query = "SELECT " + CATEGORY_COLUMN_NAME + "," + CATEGORY_COLUMN_IS_SELECTED + " FROM " + TABLE_CATEGORY ;
            res = db.rawQuery(query,null);
            if (res != null) {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public boolean updateCategorySelectedStatus(String name, String isSelected)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_COLUMN_IS_SELECTED, isSelected);

        db.update(TABLE_CATEGORY, contentValues, CATEGORY_COLUMN_NAME + "=?", new String[]{name});
        return true;
    }

    public ProfileModel getProfileData(String phone) {
        ProfileModel profileBean = new ProfileModel();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + PROFILE_COLUMN_NAME
                + "," + PROFILE_COLUMN_PHONE
                + "," + PROFILE_COLUMN_EMAIL
                + "," + PROFILE_COLUMN_SEX
                + "," + PROFILE_COLUMN_DOB
                + " FROM " + TABLE_PROFILE ;
        Cursor res = db.rawQuery(query,null);

//        Cursor res = db.query(TABLE_PROFILE, null, "phone=?", new String[]{phone}, null, null, null, null);

        if(res.getCount()<1){
            res.close();
            return null;
        }
        else if(res.getCount()>=1 && res.moveToLast()){

            profileBean.setName(res.getString(res.getColumnIndex(PROFILE_COLUMN_NAME)));
            profileBean.setPhone(res.getString(res.getColumnIndex(PROFILE_COLUMN_PHONE)));
            profileBean.setEmail(res.getString(res.getColumnIndex(PROFILE_COLUMN_EMAIL)));
            profileBean.setDob(res.getString(res.getColumnIndex(PROFILE_COLUMN_DOB)));
            profileBean.setSex(res.getString(res.getColumnIndex(PROFILE_COLUMN_SEX)));

            res.close();
            return profileBean;
        }
        return null;
    }
}
