package com.jackie.facebooktest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;
import com.jackie.facebooktest.MyLocation.LocationResult;


public class FacebookSdkTest extends Activity {
    Facebook facebook = new Facebook("109589069150352");
    String FILENAME = "AndroidSSO_data";
    private SharedPreferences mPrefs;
    
    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    
    private static final int MSG_GET_USER_INFO = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*
         * Get existing access_token if any
         */
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }
        
        /*
         * Only call authorize if the access_token has expired.
         */
        if(!facebook.isSessionValid()) {

            facebook.authorize(this, new String[] {}, new DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("access_token", facebook.getAccessToken());
                    editor.putLong("access_expires", facebook.getAccessExpires());
                    editor.commit();
                }
    
                @Override
                public void onFacebookError(FacebookError error) {}
    
                @Override
                public void onError(DialogError e) {}
    
                @Override
                public void onCancel() {}
            });
        }
        
        Log.d("qazq", ">>>>>>>>>>>>>>>>>>>>>");        
        mAsyncRunner.request("me", new RequestListener() {

            @Override
            public void onComplete(String response, Object state) {
                // TODO Auto-generated method stub
                Log.d("qazq", "response = " + response);
                Log.d("qazq", "state = " + state);
                
                // http://developers.facebook.com/tools/explorer#!/tools/explorer?method=GET&path=100000418462203
                try {
                    JSONObject jObject = new JSONObject(response);
                    String id = jObject.getString("id");
                    String name = jObject.getString("name");
                    
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("name", name);
                    Message msg = mHandle.obtainMessage(MSG_GET_USER_INFO, bundle);
                    mHandle.sendMessage(msg);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void onFacebookError(FacebookError e, Object state) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onFileNotFoundException(FileNotFoundException e,
                    Object state) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onIOException(IOException e, Object state) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onMalformedURLException(MalformedURLException e,
                    Object state) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        Log.d("qazq", "<<<<<<<<<<<<<<<<<<<<");
        
        myLocation.getLocation(this, locationResult);
    }

    MyLocation myLocation = new MyLocation();
    public LocationResult locationResult = new LocationResult() {
        @Override
        public void gotLocation(Location location) {
            Log.d("qazq", "location = " + location);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_GET_USER_INFO:
                Bundle bundle = (Bundle)msg.obj;
                String id = bundle.getString("id");
                String name = bundle.getString("name");

                String text = String.format("%s (%s)", name, id);
                TextView view = (TextView)findViewById(R.id.text);
                view.setText(text);
                
//                mAsyncRunner.logout(FacebookSdkTest.this, new RequestListener() {
//                    @Override
//                    public void onComplete(String response, Object state) {
//                        SharedPreferences.Editor editor = mPrefs.edit();
//                        editor.clear();
//                        editor.commit();
//                    }
//
//                    @Override
//                    public void onFacebookError(FacebookError e, Object state) {}
//
//                    @Override
//                    public void onFileNotFoundException(FileNotFoundException e, Object state) {}
//
//                    @Override
//                    public void onIOException(IOException e, Object state) {}
//
//                    @Override
//                    public void onMalformedURLException(MalformedURLException e, Object state) {}                    
//                });
                break;
            }
        }
    };
}