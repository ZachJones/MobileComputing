package uk.ac.lincoln.student.zachjones.mobilecomputing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainActivity extends Activity
{
    // global variable to store returned xml data from service
    static String xml = "";

    // global variable to bitmap for current number 1 single (we aren't returning the other 39 songs!)
    static Bitmap bitmap;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    //static final Uri mLocationForPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void newCatImage(View view)
    {
        bitmap = null;

        // start the  AsyncTask for calling the REST service using httpConnect class
        AsyncTaskParseXml task = new AsyncTaskParseXml();
        task.execute();
    }

    public void saveCatImage(View view)
    {
        String fileName = getFileName();
        String MEDIA_MOUNTED = "mounted";
        String diskState = Environment.getExternalStorageState();

        if(bitmap != null)
        {
            if(diskState.equals(MEDIA_MOUNTED))
            {
                File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                File pictureFile = new File(pictureFolder, fileName);

                FileOutputStream out = null;

                try
                {
                    out = new FileOutputStream(pictureFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                    out.close();
                }
                catch (IOException e)
                {
                    Toast.makeText(this, "Error saving cat Image to public directory", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(this, "No external disk mounted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName()
    {
        String file = "";

        //Finds the current date and time, then assigns it to a string variable
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String date = df.format(c.getTime());

        file = "CATappULT_" + date + ".jpeg";

        return file;
    }

    public void openImageGallery(View view)
    {
        bitmap = null;

        Intent gallery = new Intent(this, GalleryActivity.class);

        startActivity(gallery);
    }

    public void openCamera(View view)
    {
        bitmap = null;

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent image)
    {
        super.onActivityResult(requestCode, resultCode, image);

        if(resultCode == RESULT_OK)
        {
            ImageView photo = (ImageView) findViewById(R.id.imageView);
            Bitmap mBitmap = null;

            Bundle extras = image.getExtras();
            mBitmap = (Bitmap) extras.get("data");
            photo.setImageBitmap(mBitmap);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public class AsyncTaskParseXml extends AsyncTask<String, String, String>
    {
        String yourXmlServiceUrl = "http://thecatapi.com/api/images/get?format=xml";

        @Override
        // this method is used for......................
        protected void onPreExecute() {}

        @Override
        // this method is used for...................
        protected String doInBackground(String... arg0)
        {
            try {

                String text = null;
                // create new instance of the httpConnect class
                httpConnect xmlParser = new httpConnect();

                // get xml string from service url
                xml = xmlParser.getXMLFromUrl(yourXmlServiceUrl);

                // create new instance of XmlPullParser to parse xml
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                // set input to xml parser as xml string from service
                xpp.setInput( new StringReader( xml ) );

                // variable for XML parse event
                int event = xpp.getEventType();

                //Loops through xml tags to end of xml document
                while (event != XmlPullParser.END_DOCUMENT)
                {
                    String name = xpp.getName();

                    switch (event)
                    {
                        case XmlPullParser.START_TAG:
                            break;

                        case XmlPullParser.TEXT:
                            text = xpp.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            if(name.equals("url"))
                            {
                                //Parse the image url to the proper URL type
                                URL url = new URL(text);

                                //Download cat image from url and save as a bitmap
                                InputStream is = url.openConnection().getInputStream();
                                bitmap = BitmapFactory.decodeStream(is);
                            }

                            break;
                    }
                    event = xpp.next();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        //The below method will run when service HTTP request is complete
        protected void onPostExecute(String strFromDoInBg)
        {
            ImageView catImage = (ImageView) findViewById(R.id.imageView);
            catImage.setImageBitmap(bitmap);
        }
    }
}
