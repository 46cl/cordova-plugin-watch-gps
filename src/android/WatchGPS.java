package fr.velociter.cordova.watchgps;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WatchGPS extends CordovaPlugin {

  private static final String TAG = "CordovaWatchGPS";

  private enum Status {
    DISABLED,
    ENABLED
  }

  private LocationListener listener;

  private LocationManager locationManager;

  private List<CallbackContext> subscribers;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    Log.i(TAG, "WatchGPS plugin initialized");


    locationManager = (LocationManager) cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);

    subscribers = new ArrayList<CallbackContext>();
    listener = new LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
      }

      @Override
      public void onStatusChanged(String provider, int status, Bundle extras) {
      }

      @Override
      public void onProviderEnabled(String provider) {
        Log.i(TAG, "Provider enabled : [" + provider + "]");
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
          Log.i(TAG, "GPS provider enabled");
          reportStatus(Status.ENABLED);
        }
      }

      @Override
      public void onProviderDisabled(String provider) {
        Log.i(TAG, "Provider disabled : [" + provider + "]");
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
          Log.i(TAG, "GPS provider disabled");
          reportStatus(Status.DISABLED);
        }
      }
    };

    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, listener);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    Log.i(TAG, "WatchGPS execute [" + action + "]");
    if (action.equals("subscribe")){
      subscribers.add(callbackContext);
      PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);
      return true;
    }
    return false;
  }

  private void reportStatus(Status status) {
    JSONObject data = new JSONObject();

    try {
      data.put("status", status.toString().toLowerCase());
    } catch (JSONException e) {
      Log.e(TAG, "Failed to report GPS status", e);
    }

    for (CallbackContext subscriber : subscribers) {
      PluginResult result = new PluginResult(PluginResult.Status.OK, data);
      result.setKeepCallback(true);
      subscriber.sendPluginResult(result);
    }
  }
}



