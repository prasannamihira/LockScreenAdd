package com.crowderia.mytoz.model;

import java.io.Serializable;

/**
 * Created by crowderia on 11/1/16.
 */

public class CategoryModel implements Serializable {

    private String categoryName;
    private String isInterested;

    public CategoryModel(String categoryName, String isInterested) {
        this.categoryName = categoryName;
        this.isInterested = isInterested;
    }



    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String isInterested() {
        return isInterested;
    }

    public void setInterested(String interest) {
        isInterested = interest;
    }
}
