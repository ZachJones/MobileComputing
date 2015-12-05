package uk.ac.lincoln.student.zachjones.mobilecomputing;

import android.graphics.Bitmap;

import java.io.File;

public class GridItem {
    private File imageLocation;

    public GridItem() {
        super();
    }

    public File getImage() {
        return imageLocation;
    }

    public void setImage(File imageLocation) {this.imageLocation = imageLocation;}
}