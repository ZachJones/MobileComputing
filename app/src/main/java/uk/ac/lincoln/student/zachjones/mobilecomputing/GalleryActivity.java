package uk.ac.lincoln.student.zachjones.mobilecomputing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class GalleryActivity extends Activity
{
    private File pictureFolder;
    private ArrayList<File> imageFile = new ArrayList<File>();
    private GridView gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gallery = (GridView) findViewById(R.id.imageGrid);

        getSavedImages();
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
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        ArrayAdapter<Bitmap> images = new ArrayAdapter<Bitmap>(GalleryActivity.this, android.R.layout.simple_gallery_item);

        FileInputStream fis = null;
        BufferedInputStream bis = null;

        for (int i = 0; i < imageFile.size(); i++)
        {
            fileName = "";
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
                    //bitmap = BitmapFactory.decodeFile(fileName, options); //bitmap == null!!!!

                    fis = new FileInputStream(current); //Throws exception, need directories?
                    bis = new BufferedInputStream(fis);
                    bitmap = BitmapFactory.decodeStream(bis);
                    //Bitmap useThisBitmap = Bitmap.createScaledBitmap(bitmap, parent.getWidth(), parent.getHeight(), true);

                    images.add(bitmap);
                }
            }
            catch (Exception e)
            {
                //Toast.makeText(this, "Incorrect filetype", Toast.LENGTH_SHORT);
            }
        }

        gallery.setAdapter(images); //App breaks just before here. Why??
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