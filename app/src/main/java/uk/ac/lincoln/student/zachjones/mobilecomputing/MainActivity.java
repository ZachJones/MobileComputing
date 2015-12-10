/**
 * Zach Jones (JON11356270), University of Lincoln
 *
 * Mobile Computing (CMP3109M-1516), Assessment 1
 *
 * All code used follows Android's "Code Style for Contributors" guidelines:
 *
 *          https://source.android.com/source/code-style.html
 *
 * References:
 *          http://derekfoster.cloudapp.net/mc/workshop5.htm
 *          http://www.w3schools.com/sql/sql_insert.asp
 * */

package uk.ac.lincoln.student.zachjones.mobilecomputing;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity
{
    public static final int REQUEST_IMAGE_CAPTURE = 1; //Set up intent to use the phone's camera
    public static SQLiteDatabase sDatabase; //Global variable to store the SQLite sDatabase
    static String sXML = ""; //Global variable to store returned XML data from The Cat API
    static Bitmap sBitmap; //Global variable to store sBitmap from the URL

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Attach XML layout to MainActivity
    }

    /** Calls methods to get and store XML data */
    public void newCatImage(View view)
    {
        sBitmap = null; //Reinitialise sBitmap

        //If the "xmlData" sDatabase is not present on the phone, create it; then open the sDatabase
        sDatabase = openOrCreateDatabase("xmlData", MODE_PRIVATE, null);
        createTable();

        //Start the task for calling the REST service asynchronously,
        // so that other processes can still be completed whilst the XML data is parsed
        AsyncTaskParseXml task = new AsyncTaskParseXml();
        task.execute();
    }

    /** Defines table in the SQLite database if one is not present */
    private void createTable()
    {
        String createXMLTable = "CREATE TABLE IF NOT EXISTS Image_Data " +
                "(imageID TEXT, imageURL TEXT, sourceURL TEXT);"; //Define table columns in SQL
        sDatabase.execSQL(createXMLTable); //Create table with columns for each data item
    }

    /** Saves the currently displayed bitmap to external storage */
    public void saveCatImage(View view)
    {
        FileOutputStream out; //The file stream through which the sBitmap is saved
        String fileName = getFileName(); //Creates a file name for the sBitmap to be stored under

        //Define variables used to error handle external storage
        String MEDIA_MOUNTED = "mounted";
        String diskState = Environment.getExternalStorageState();

        //If a image is present in the activity, save it to external storage
        if(sBitmap != null)
        {
            if(diskState.equals(MEDIA_MOUNTED))
            {
                //Set the file path to be used, within the SD card's "Pictures" directory
                File pictureFolder = Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_PICTURES);
                File pictureFile = new File(pictureFolder, fileName);

                try
                {
                    out = new FileOutputStream(pictureFile); //Open the file stream

                    //Compress the sBitmap to 80% quality and store it via the file stream
                    sBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

                    out.close(); //Close the file stream to avoid a memory leak

                    Toast.makeText(this, "Image saved to SD card", Toast.LENGTH_SHORT).show();
                }
                catch (IOException e)
                {
                    Toast.makeText(this, "Error saving cat Image to public directory",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); //Use for error troubleshooting
                }
            }
            else
            {
                Toast.makeText(this, "No external disk mounted", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "No image loaded, please take a picture or get a cat",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /** Creates a unique file name for each image using the current date, time and app name */
    private String getFileName()
    {
        String file; //Variable for created filename

        //Finds the current date and time, then assign it to the string variable
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String date = df.format(c.getTime());

        //Concatenate the app name and file type to the date so that a unique filing style is used
        file = "CATappULT_" + date + ".jpeg";

        return file;
    }

    public void openImageGallery(View view)
    {
        sBitmap = null;

        //Create intent and begin GalleryActivity
        Intent gallery = new Intent(this, GalleryActivity.class);
        startActivity(gallery);
    }

    public void openCamera(View view)
    {
        sBitmap = null;

        //Create intent to access the phone's camera and begin activity if the intent is successful
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    /** Sets the image view to an image captured using the camera when focus returns to CATappULT
     *
     * NOTE: An unknown bug causes the app to break here during testing in Lab A,
     * but Lab B returns images to MainActivity successfully */
    protected void onActivityResult(int requestCode, int resultCode, Intent image)
    {
        super.onActivityResult(requestCode, resultCode, image);

        ImageView catImage = (ImageView) findViewById(R.id.imageView);

        //If an image was taken, set the image view and sBitmap variable to the captured image
        if(resultCode == RESULT_OK)
        {
            Bundle extras = image.getExtras();
            sBitmap = (Bitmap) extras.get("data");
            catImage.setImageBitmap(sBitmap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.add(menu.NONE, 2, 103, "Info");

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

    /** Executes a GET request to The Cat API REST service asynchronously,
     * obtaining a bitmap from URL and storing the XML data into a SQLite database */
    public class AsyncTaskParseXml extends AsyncTask<String, String, String>
    {
        //Set the REST service URL to a variable
        String mRESTServiceUrl = "http://thecatapi.com/api/images/get?format=xml";

        ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        @Override
        /** Invoked before the task is executed */
        protected void onPreExecute()
        {
            mProgressBar.setVisibility(View.VISIBLE); //Show progress bar
        }

        @Override
        /** Execute asynchronous task immediately after onPreExecute() */
        protected String doInBackground(String... arg0)
        {
            try
            {
                //Initialise variables
                String text = null;
                String imageURL = null;
                String imageID = null;
                String sourceURL = null;

                //Create new instance of the httpConnect class
                httpConnect xmlParser = new httpConnect();

                //Get XML string from REST service URL
                sXML = xmlParser.getXMLFromUrl(mRESTServiceUrl);

                //Create new instance of XmlPullParser to parse XML
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                //Set the input to XMLPullParser as the XML string taken from The Cat API
                xpp.setInput(new StringReader(sXML));

                //Define variable for XML parse event
                int event = xpp.getEventType();

                //Loops through each XML tag to end of the XML document
                while (event != XmlPullParser.END_DOCUMENT)
                {
                    String name = xpp.getName(); //Set the current tag to a variable

                    switch (event)
                    {
                        //Start tag
                        case XmlPullParser.START_TAG:
                            break;

                        //Text within tags
                        case XmlPullParser.TEXT:
                            //Set the current text (within a tag) to a variable
                            text = xpp.getText();
                            break;

                        //End tag
                        case XmlPullParser.END_TAG:
                            try
                            {
                                //If the current tag contains data we want to parse...
                                if (name.equals("url"))
                                {
                                    //Parse the image url to the proper URL type
                                    URL url = new URL(text);

                                    imageURL = text; //Store URL for sDatabase usage

                                    //Download cat image from the URL and save as a sBitmap
                                    InputStream is = url.openConnection().getInputStream();
                                    sBitmap = BitmapFactory.decodeStream(is);
                                }
                                else if(name.equals("id"))
                                {
                                    imageID = text; //Store image ID for sDatabase usage
                                }
                                else if(name.equals("source_url"))
                                {
                                    sourceURL = text; //Store source URL for sDatabase usage
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e("Cat API Parsing", "Invalid data from: " + name);
                            }
                            break;
                    }
                    event = xpp.next(); //Parse next tag
                }

                sDatabase.execSQL("INSERT INTO Image_Data VALUES (\""
                        + imageID + "\", \""
                        + imageURL + "\", \""
                        + sourceURL + "\");"); //Store XML data into xmlData sDatabase
            }
            catch (Exception e)
            {
                e.printStackTrace(); //Use for error troubleshooting
            }

            sDatabase.close(); //Close sDatabase to avoid memory leaks
            return null;
        }

        @Override
        /** The below method will run when service HTTP request is complete */
        protected void onPostExecute(String strFromDoInBg)
        {
            mProgressBar.setVisibility(View.INVISIBLE); //Hide progress bar

            //No XML data was obtained from The Cat API
            if (sXML == null)
            {
                Toast.makeText(MainActivity.this,
                        "Error loading images, are you connected to the internet?",
                        Toast.LENGTH_SHORT).show();
            }

            //Set image view to the image obtained from The Cat API REST service
            ImageView catImage = (ImageView) findViewById(R.id.imageView);
            catImage.setImageBitmap(sBitmap);
        }
    }
}
