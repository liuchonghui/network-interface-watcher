package tools.android.networkinterfacewatcher;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static boolean checkNetworkState(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkWifiState(Context context) {
        try {
            ConnectivityManager connectManager = (ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connectManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifi.isAvailable() && wifi.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isNetworkHardwareAvailable(Context context) {
        if (isWifiHardwareAvailable(context) || isMobileHardwareAvailable(context)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isWifiHardwareAvailable(Context context) {
        try {
            ConnectivityManager connectManager = (ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifi = connectManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifi != null && wifi.isAvailable()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMobileHardwareAvailable(Context context) {
        try {
            ConnectivityManager connectManager = (ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo mobile = connectManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobile != null && mobile.isAvailable()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
