package com.example.penpal.interests.model;

import com.example.penpal.R;

public class Interest {

    private String mImageLink;
    private String mInterestName;
    private String mInterestType;
    private boolean mInterestClicked;

    public Interest(String mImageLink, String mInterestName, String mInterestType){
        this.mImageLink = mImageLink;
        this.mInterestName = mInterestName;
        this.mInterestType = mInterestType;
        this.mInterestClicked = false;
    }

    public void itemClicked(){
        //this.mImageLink = "drawable://" + R.drawable.ic_check_circle_black_24dp;
        mImageLink = "none";
        mInterestName = "clicked";

    }

    public String getmImageLink() {
        return mImageLink;
    }

    public void setmImageLink(String mImageLink) {
        this.mImageLink = mImageLink;
    }

    public String getmInterestName() {
        return mInterestName;
    }

    public void setmInterestName(String mInterestName) {
        this.mInterestName = mInterestName;
    }

    public String getmInterestType() {
        return mInterestType;
    }

    public void setmInterestType(String mInterestType) {
        this.mInterestType = mInterestType;
    }

    public boolean ismInterestClicked(){return mInterestClicked;}

    public void setmInterestClicked(boolean isClicked){this.mInterestClicked = isClicked;}
}
