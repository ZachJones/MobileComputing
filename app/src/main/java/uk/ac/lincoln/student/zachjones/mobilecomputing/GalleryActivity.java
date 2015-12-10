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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class GalleryActivity extends Activity
{
    private ArrayList<File> mImageFile = new ArrayList<File>();
    private ImageView mLargeImage;
    private Button mDeleteButton;
    private Button mReturnButton;
    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private File mSelectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mGridView = (GridView) findViewById(R.id.imageGrid);
        mLargeImage = (ImageView) findViewById(R.id.largeImage);
        mDeleteButton = (Button) findViewById(R.id.deleteButton);
        mReturnButton = (Button) findViewById(R.id.returnButton);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);

        //Set up the onClickListener for each GridView item
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            /** Sets the larger image view's bitmap to the same as the clicked image, then makes it visible */
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                FileInputStream fis;
                BufferedInputStream bis;

                try
                {
                    GridItem item = (GridItem) parent.getItemAtPosition(position);
                    mSelectedImage = item.getImage();
                    fis = new FileInputStream(mSelectedImage);
                    bis = new BufferedInputStream(fis);
                    Bitmap bitmap = BitmapFactory.decodeStream(bis);

                    mLargeImage.setImageBitmap(bitmap);
                }
                catch (Exception e)
                {
                    Toast.makeText(GalleryActivity.this, "Error loading image",
                            Toast.LENGTH_SHORT).show();
                }

                mGridView.setVisibility(View.INVISIBLE);
                mLargeImage.setVisibility(View.VISIBLE);
                mDeleteButton.setVisibility(View.VISIBLE);
                mReturnButton.setVisibility(View.VISIBLE);
            }
        });

        getSavedImages();
    }

    /** Deletes the selected image from external storage */
    public void deleteImage(View view)
    {
        if(mLargeImage != null)
        {
            try
            {
                mSelectedImage.delete();
            }
            catch (Exception e)
            {
                Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
            }
        }

        showGallery(view);
    }

    /** Restarts the activity, in order to update the grid view */
    public void showGallery(View view)
    {
        try
        {
            //Reload the Gallery activity
            Intent reload = new Intent(GalleryActivity.this, GalleryActivity.class);
            startActivity(reload);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error loading gallery, please try again later",
                    Toast.LENGTH_SHORT).show();
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        }
    }

    /** Calls other methods to create the image grid */
    private void getSavedImages()
    {
        String MEDIA_MOUNTED = "mounted";
        String diskState = Environment.getExternalStorageState();

        if(diskState.equals(MEDIA_MOUNTED))
        {
            File pictureFolder = Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_PICTURES);
            getFile(pictureFolder);

            setupImageGrid(mImageFile);
        }
        else
        {
            Toast.makeText(this, "No external disk mounted", Toast.LENGTH_SHORT).show();
        }
    }

    /** Creates an image view for each image stored in imageFile and
     * uses the GridViewAdapter class to put them into a grid */
    private void setupImageGrid(ArrayList<File> imageFile)
    {
        String fileName;
        File current;
        String fileStart;
        GridItem image;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        for (int i = 0; i < imageFile.size(); i++)
        {
            fileStart = "";
            current = imageFile.get(i);
            fileName = current.getName();

            for (int n = 0; n < 9; n++) //Loop up to 10 as there are 9 letters in CATappULT
            {
                fileStart+= fileName.charAt(n);
            }

            try
            {
                //Finds all images that were saved using CATappULT on external storage
                //and creates a corresponding GridItem
                if (fileStart.equals("CATappULT"))
                {
                    image = new GridItem();

                    image.setImage(current);

                    mGridData.add(image);
                }
            }
            catch (Exception e)
            {
                Log.e("Gallery Activity", "Error loading images " + e.toString());
                Intent main = new Intent(GalleryActivity.this, MainActivity.class);
                startActivity(main);
            }
        }

        mGridAdapter.setGridData(mGridData);
    }

    /** Retrieves all .jpeg files stored on the SD card and saves them to the mImageFile array */
    public ArrayList<File> getFile(File dir)
    {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0)
        {
            for (int i = 0; i < listFile.length; i++)
            {

                if (listFile[i].isDirectory())
                {
                    getFile(listFile[i]);
                }
                else
                {
                    if (listFile[i].getName().endsWith(".jpeg"))
                    {
                        mImageFile.add(listFile[i]);
                    }
                }

            }
        }

        return mImageFile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);

        menu.add(menu.NONE, 2, 103, "Info"); //Adds an information option to the menu

        return true;
    }

    @Override
    /** Opens the phone's memory settings */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            startActivity(new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS));

            return true;
        }
        else
        {
            Toast.makeText(this, "CATappULT version 1.0", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}