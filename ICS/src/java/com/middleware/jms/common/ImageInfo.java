package com.middleware.jms.common;

import java.io.Serializable;

public class ImageInfo implements Serializable {
    
    public enum Option {
        CROP,
        RESIZE
    }
    
    private String[] images;
    private Option option;
    private boolean applyGreyScale;
    private Integer uploadSessionID;

    public Integer getUploadSessionID() {
        return uploadSessionID;
    }

    public void setUploadSessionID(Integer uploadSessionID) {
        this.uploadSessionID = uploadSessionID;
    }


    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public boolean isApplyGreyScale() {
        return applyGreyScale;
    }

    public void setApplyGreyScale(boolean applyGreyScale) {
        this.applyGreyScale = applyGreyScale;
    }
    
    
    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }
    
    @Override
    public String toString() {
        String ans = "##########\n";
        ans+= "- "+images[0]+"\n";
        ans+= "- "+images[1]+"\n";
        ans+= "- "+images[2]+"\n";
        ans+= "- "+images[3]+"\n";
        ans+= "##########\n";
        
        return ans;
    }
    
}
