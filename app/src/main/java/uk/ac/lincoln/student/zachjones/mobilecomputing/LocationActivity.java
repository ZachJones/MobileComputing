package uk.ac.lincoln.student.zachjones.mobilecomputing;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class LocationActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    GoogleApiClient mGoogleApiClient;
    double mLatitudeText = 0;
    double mLongitudeText = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        buildGoogleApiClient();

        //SIMPLE INSTANCING
        //Create the activity
        super.onCreate(savedInstanceState);

        //Get the message from the intent started in the helloWorld activity
        Intent intent = getIntent();
        String message = intent.getStringExtra("testParameter");

        //Create a new TextView widget programmatically
        TextView textView = new TextView(this);
        textView.setTextSize(40);

        /*Set the TextView to the string message -
        which was passed as a parameter from the HelloWorld activity*/
        textView.setText(message);

        //mGoogleApiClient.connect();

        //LOCATION SERVICES
        textView.setText("Location:\n" + mLatitudeText + "\n" + mLongitudeText);

        //Set the TextView widget as the activity UI layout
        setContentView(textView);
    }


    protected void onStart()
    {
        super.onStart();

        mGoogleApiClient.connect();
    }

    protected void onStop()
    {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint)
    {
        Location mLastLocation;

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText = mLastLocation.getLatitude();
            mLongitudeText = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
