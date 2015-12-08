package uk.ac.lincoln.student.zachjones.mobilecomputing;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class GalleryActivity extends Activity
{
    private File pictureFolder;
    private ArrayList<File> imageFile = new ArrayList<File>();
    private ImageView mLargeImage;
    private Button mDeleteButton;
    private Button mReturnButton;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private File selectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mGridView = (GridView) findViewById(R.id.imageGrid);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mLargeImage = (ImageView) findViewById(R.id.largeImage);
        mDeleteButton = (Button) findViewById(R.id.deleteButton);
        mReturnButton = (Button) findViewById(R.id.returnButton);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);

        //Set up the onClickListener for each GridView item
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                FileInputStream fis = null;
                BufferedInputStream bis = null;
                selectedImage = item.getImage();

                try
                {
                    fis = new FileInputStream(selectedImage);
                    bis = new BufferedInputStream(fis);
                    Bitmap bitmap = BitmapFactory.decodeStream(bis);

                    mLargeImage.setImageBitmap(bitmap);
                }
                catch (Exception e)
                {
                    //Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }

                mGridView.setVisibility(View.INVISIBLE);
                mLargeImage.setVisibility(View.VISIBLE);
                mDeleteButton.setVisibility(View.VISIBLE);
                mReturnButton.setVisibility(View.VISIBLE);
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);

        getSavedImages();
    }

    public void deleteImage(View view)
    {
        if(mLargeImage != null)
        {
            try
            {
                selectedImage.delete();
            }
            catch (Exception e)
            {
                Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
            }
        }

        showGallery(view);
    }

    public void showGallery(View view)
    {
        //Reload the Gallery activity
        Intent intent = new Intent(GalleryActivity.this, GalleryActivity.class);
        startActivity(intent);
    }

    private void getSavedImages()
    {
        String MEDIA_MOUNTED = "mounted";
        String diskState = Environment.getExternalStorageState();

        if(diskState.equals(MEDIA_MOUNTED))
        {
            File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            getfile(pictureFolder);

            setupImageGrid(imageFile);
        }
        else
        {
            Toast.makeText(this, "No external disk mounted", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupImageGrid(ArrayList<File> imageFile)
    {
        String fileName;
        File current;
        String fileStart;
        GridItem image;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ArrayAdapter<Bitmap> images = new ArrayAdapter<Bitmap>(GalleryActivity.this, android.R.layout.simple_gallery_item);

        for (int i = 0; i < imageFile.size(); i++)
        {
            fileStart = "";
            current = imageFile.get(i);
            fileName = current.getName();


            //NB - Might need to discount the folder names (pictureFolder) before getting fileStart

            for (int n = 0; n < 9; n++) //Loop up to 10 as there are 9 letters in CATappULT
            {
                fileStart+= fileName.charAt(n);
            }

            try
            {
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
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }

        mGridAdapter.setGridData(mGridData);
        mProgressBar.setVisibility(View.GONE);
    }

    public ArrayList<File> getfile(File dir)
    {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0)
        {
            for (int i = 0; i < listFile.length; i++)
            {

                if (listFile[i].isDirectory())
                {
                    //images.add(listFile[i]);
                    getfile(listFile[i]);
                }
                else
                {
                    if (listFile[i].getName().endsWith(".jpeg"))
                    {
                        imageFile.add(listFile[i]);
                    }
                }

            }
        }
        return imageFile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}