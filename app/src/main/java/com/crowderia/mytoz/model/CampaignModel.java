package com.crowderia.mytoz.model;

import java.io.Serializable;

/**
 * Created by crowderia on 11/1/16.
 */

public class CampaignModel implements Serializable {

    private String campaignId;
    private String campaignName;
    private String imageUrl;
    private String imageUrlLockScreen;
    private String category;
    private String campaignAddDate;
    private String campaignWebUrl;

    public CampaignModel(String campaignId, String imageUrl, String imageUrlLockScreen, String name, String category, String webUrl) {
        this.campaignId = campaignId;
        this.imageUrl = imageUrl;
        this.imageUrlLockScreen = imageUrlLockScreen;
        this.campaignName = name;
        this.category = category;
        this.campaignWebUrl = webUrl;
    }

    public CampaignModel() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageUrlLockScreen() {
        return imageUrlLockScreen;
    }

    public void setImageUrlLockScreen(String imageUrlLockScreen) {
        this.imageUrlLockScreen = imageUrlLockScreen;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getCampaignAddDate() {
        return campaignAddDate;
    }

    public void setCampaignAddDate(String campaignAddDate) {
        this.campaignAddDate = campaignAddDate;
    }

    public String getCampaignWebUrl() {
        return campaignWebUrl;
    }

    public void setCampaignWebUrl(String campaignWebUrl) {
        this.campaignWebUrl = campaignWebUrl;
    }
}
