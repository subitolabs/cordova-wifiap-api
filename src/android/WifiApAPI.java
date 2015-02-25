package com.subitolabs.cordova.wifiapapi;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiApAPI extends CordovaPlugin {

    private static int constant = 0;

    private static final int WIFI_AP_STATE_UNKNOWN = -1;
    private static int WIFI_AP_STATE_DISABLING = 0;
    private static int WIFI_AP_STATE_DISABLED = 1;
    public int WIFI_AP_STATE_ENABLING = 2;
    public int WIFI_AP_STATE_ENABLED = 3;
    private static int WIFI_AP_STATE_FAILED = 4;

    public static final String ACTION_ENABLE_AP = "setApEnabled";
    public static final String ACTION_DISABLE_AP = "setApDisabled";

    private final String[] WIFI_STATE_TEXTSTATE = new String[] {
            "DISABLING","DISABLED","ENABLING","ENABLED","FAILED"
    };

    private WifiManager wifi;
    private String TAG = "WifiAP";

    private String SSID = "";

    private int stateWifiWasIn = -1;

    private boolean alwaysEnableWifi = true; //set to false if you want to try and set wifi state back to what it was before wifi ap enabling, true will result in the wifi always being enabled after wifi ap is disabled


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
    {

        try {
            if (ACTION_ENABLE_AP.equals(action))
            {

                SSID = args.getString(0);

                setWifiApEnabled(true);

                callbackContext.success(new JSONArray());

                return true;
            }
            else if (ACTION_DISABLE_AP.equals(action))
            {

                setWifiApEnabled(false);

                callbackContext.success(new JSONArray());

                return true;
            }
            callbackContext.error("Invalid action");
            return false;
        } catch(Exception e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
            return false;
        }
    }

    /**
     * Enable/disable wifi
     * @param true or false
     * @return WifiAP state
     */
    private void setWifiApEnabled(boolean enabled) {

        wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

        Log.d(TAG, "*** setWifiApEnabled CALLED **** " + enabled);

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = SSID;
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);


        //remember wirelesses current state
        if (enabled && stateWifiWasIn==-1){
            stateWifiWasIn=wifi.getWifiState();
        }

        //disable wireless
        if (enabled && wifi.getConnectionInfo() !=null) {
            Log.d(TAG, "disable wifi: calling");
            wifi.setWifiEnabled(false);
            int loopMax = 10;
            while(loopMax>0 && wifi.getWifiState()!=WifiManager.WIFI_STATE_DISABLED){
                Log.d(TAG, "disable wifi: waiting, pass: " + (10-loopMax));
                try {
                    Thread.sleep(500);
                    loopMax--;
                } catch (Exception e) {

                }
            }
            Log.d(TAG, "disable wifi: done, pass: " + (10-loopMax));
        }

        //enable/disable wifi ap
        int state = WIFI_AP_STATE_UNKNOWN;
        try {
            Log.d(TAG, (enabled?"enabling":"disabling") +" wifi ap: calling");
            wifi.setWifiEnabled(false);
            Method method1 = wifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            //method1.invoke(wifi, null, enabled); // true
            method1.invoke(wifi, config, enabled); // true
            Method method2 = wifi.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(wifi);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            // toastText += "ERROR " + e.getMessage();
        }

        //hold thread up while processing occurs
        if (!enabled) {
            int loopMax = 10;
            while (loopMax>0 && (getWifiAPState()==WIFI_AP_STATE_DISABLING || getWifiAPState()==WIFI_AP_STATE_ENABLED || getWifiAPState()==WIFI_AP_STATE_FAILED)) {
                Log.d(TAG, (enabled?"enabling":"disabling") +" wifi ap: waiting, pass: " + (10-loopMax));
                try {
                    Thread.sleep(500);
                    loopMax--;
                } catch (Exception e) {

                }
            }
            Log.d(TAG, (enabled?"enabling":"disabling") +" wifi ap: done, pass: " + (10-loopMax));

            //enable wifi if it was enabled beforehand
            //this is somewhat unreliable and app gets confused and doesn't turn it back on sometimes so added toggle to always enable if you desire
            if(stateWifiWasIn==WifiManager.WIFI_STATE_ENABLED || stateWifiWasIn==WifiManager.WIFI_STATE_ENABLING || stateWifiWasIn==WifiManager.WIFI_STATE_UNKNOWN || alwaysEnableWifi){
                Log.d(TAG, "enable wifi: calling");
                wifi.setWifiEnabled(true);
                //don't hold things up and wait for it to get enabled
            }

            stateWifiWasIn = -1;
        } else if (enabled) {
            int loopMax = 10;
            while (loopMax>0 && (getWifiAPState()==WIFI_AP_STATE_ENABLING || getWifiAPState()==WIFI_AP_STATE_DISABLED || getWifiAPState()==WIFI_AP_STATE_FAILED)) {
                Log.d(TAG, (enabled?"enabling":"disabling") +" wifi ap: waiting, pass: " + (10-loopMax));
                try {
                    Thread.sleep(500);
                    loopMax--;
                } catch (Exception e) {

                }
            }
            Log.d(TAG, (enabled?"enabling":"disabling") +" wifi ap: done, pass: " + (10-loopMax));
        }

    }

    /**
     * Get the wifi AP state
     * @return WifiAP state
     * @author http://stackoverflow.com/a/7049074/1233435
     */
    public int getWifiAPState() {
        int state = WIFI_AP_STATE_UNKNOWN;
        try {
            Method method2 = wifi.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(wifi);
        } catch (Exception e) {

        }

        if(state>=10){
            //using Android 4.0+ (or maybe 3+, haven't had a 3 device to test it on) so use states that are +10
            constant=10;
        }

        //reset these in case was newer device
        WIFI_AP_STATE_DISABLING = 0+constant;
        WIFI_AP_STATE_DISABLED = 1+constant;
        WIFI_AP_STATE_ENABLING = 2+constant;
        WIFI_AP_STATE_ENABLED = 3+constant;
        WIFI_AP_STATE_FAILED = 4+constant;

        Log.d(TAG, "getWifiAPState.state " + (state==-1?"UNKNOWN":WIFI_STATE_TEXTSTATE[state-constant]));

        return state;
    }


    private Context getContext()
    {
        return this.cordova.getActivity().getApplicationContext();
    }

    private class Object extends JSONObject
    {

    }

    private class ArrayOfObjects extends ArrayList<Object>
    {

    }
}
