package com.nguyenhoanglam.imagepicker.model;

import java.util.ArrayList;

/**
 * Created by boss1088 on 8/22/16.
 */
public class Folder {

    private String folderName;
    private ArrayList<Asset> images;

    public Folder(String bucket) {
        folderName = bucket;
        images = new ArrayList<>();
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public ArrayList<Asset> getImages() {
        return images;
    }

    public void setImages(ArrayList<Asset> images) {
        this.images = images;
    }
}
