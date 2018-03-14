package com.crowderia.mytoz.util;

import com.crowderia.mytoz.model.CampaignModel;
import com.crowderia.mytoz.model.CategoryModel;

/**
 * Created by Crowderia on 11/10/2016.
 */

public class Workspace {

    CampaignModel campaignModel;
    CategoryModel interestModel;
    private static Workspace instance = null;
    public static synchronized Workspace getInstance(){
        if (instance == null){
            instance = new Workspace();
        }
        return instance;
    }

    private Workspace(){
        initialize();
    }

    public void initialize(){
//        setCampaignModel(new CampaignModel());
//        setInterestModel(new CategoryModel());
    }

    public CampaignModel getCampaignModel() {
        return campaignModel;
    }

    public CategoryModel getInterestModel() {
        return interestModel;
    }

    public void setCampaignModel(CampaignModel campaignModel) {
        this.campaignModel = campaignModel;
    }

    public void setInterestModel(CategoryModel interestModel) {
        this.interestModel = interestModel;
    }
}
