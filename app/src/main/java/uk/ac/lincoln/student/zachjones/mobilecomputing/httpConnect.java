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
 *          http://derekfoster.cloudapp.net/mc/workshop5.htm
 * */

package uk.ac.lincoln.student.zachjones.mobilecomputing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.util.Log;
import android.widget.Toast;

public class httpConnect
{
    public static final String TAG = "XMLParser.java";
    static String sXml = "";

    /** Configures a connection to The Cat API REST service and
     * forms a GET request in order to receive the XML data stored at the URL */
    public String getXMLFromUrl(String url)
    {
        try
        {
            URL u = new URL(url);
            HttpURLConnection restConnection = (HttpURLConnection) u.openConnection();
            restConnection.setRequestMethod("GET");
            restConnection.setRequestProperty("Content-length", "0");
            restConnection.setUseCaches(false);
            restConnection.setAllowUserInteraction(false);
            restConnection.setConnectTimeout(10000);
            restConnection.setReadTimeout(10000);
            restConnection.connect();
            int status = restConnection.getResponseCode();

            //Catches HTTP 200 and 201 errors
            switch (status) {
                case 200:
                case 201:
                    //Live connection to the REST service is established
                    BufferedReader br = new BufferedReader
                            (new InputStreamReader(restConnection.getInputStream()));

                    //String builder variable to store XML data
                    StringBuilder sb = new StringBuilder();
                    String line;

                    //Loop through returned data line by line and append to StringBuilder variable
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();

                    try
                    {
                        sXml = sb.toString(); //Store XML as a string
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Error parsing data " + e.toString());
                    }

                    //Return the string containing XML data to MainActivity
                    return sXml;
            }
        }
        //HTTP 200 and 201 error handling from switch statement
        catch (MalformedURLException ex)
        {
            Log.e(TAG, "Malformed URL ");
        }
        catch (IOException ex)
        {
            Log.e(TAG, "IO Exception ");
        }

        return null;
    }
}
