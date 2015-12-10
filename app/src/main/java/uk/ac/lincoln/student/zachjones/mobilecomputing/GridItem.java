/**
 * Zach Jones (JON11356270), University of Lincoln
 *
 * Mobile Computing (CMP3109M-1516), Assessment 1
 *
 * All code used follows Android's "Code Style for Contributors" guidelines:
 *
 *          https://source.android.com/source/code-style.html
 *
 * Reference:
 *          http://javatechig.com/android/download-and-display-image-in-android-gridview
 * */

package uk.ac.lincoln.student.zachjones.mobilecomputing;

import java.io.File;

public class GridItem
{
    private File mImageLocation;

    public GridItem() {
        super();
    }

    public File getImage() {return mImageLocation;}

    public void setImage(File imageLocation) {this.mImageLocation = imageLocation;}
}