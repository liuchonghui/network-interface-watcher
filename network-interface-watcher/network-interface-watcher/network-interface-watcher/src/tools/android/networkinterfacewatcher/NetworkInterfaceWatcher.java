package tools.android.networkinterfacewatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class NetworkInterfaceWatcher extends BroadcastReceiver {

    private boolean enableLogcat = false;
    private boolean stop = false;
    private boolean debounce = false;
    private long debounceMillis = 0L;
    private Debouncer debouncer;

    private Handler mHandler = new Handler(new HandlerThread("NetworkInterfaceWatcher-single-thread") {{
        start();
    }}.getLooper());

    public NetworkInterfaceWatcher() {
        this(false, 0L);
    }

    public NetworkInterfaceWatcher(boolean enableLogcat) {
        this(enableLogcat, 0L);
    }

    public NetworkInterfaceWatcher(long debounceMillis) {
        this(false, debounceMillis);
    }

    public NetworkInterfaceWatcher(boolean enableLogcat, long debounceMillis) {
        this.enableLogcat = enableLogcat;
        if (debounceMillis <= 0) {
            this.debounce = false;
        } else if (debounceMillis < 100) {
            this.debounce = true;
            this.debounceMillis = 100L;
            this.debouncer = new Debouncer(this.debounceMillis);
        } else if (debounceMillis > 5000) {
            this.debounce = true;
            this.debounceMillis = 5000L;
            this.debouncer = new Debouncer(this.debounceMillis);
        } else {
            this.debounce = true;
            this.debounceMillis = debounceMillis;
            this.debouncer = new Debouncer(this.debounceMillis);
        }
    }

    protected void networkUnavailable() {
//        Log.i("NetworkWatcher", "network module unavailable");
        say("NetworkWatcher", "call networkUnavailable");
    }

    /**
     * This method is called when the NetworkInterfaceWatcher is receiving a
     * change in network connectivity has occurred. Indicating that network
     * connectivity module is valid.
     */
    protected void networkHardwareAvailable() {
//        Log.i("NetworkWatcher", "network module available");
    }

    /**
     * This method is called when the NetworkInterfaceWatcher is receiving a
     * change in network connectivity has occurred. Indicating that network
     * connectivity module is invalid.
     */
    protected void networkHardwareInvalid() {
//        Log.i("NetworkWatcher", "network module missing");
    }

    /**
     * This method is called when the NetworkInterfaceWatcher is receiving a
     * change in network connectivity has occurred. Indicating that network
     * connectivity is connecting in use.
     */
    protected void networkConnect() {
//        Log.i("NetworkWatcher", "network connecting");
    }

    /**
     * This method is called when the NetworkInterfaceWatcher is receiving a
     * change in network connectivity has occurred. Indicating that network
     * connectivity is connecting refuse.
     */
    protected void networkDisconnect() {
//        Log.i("NetworkWatcher", "network disconnect");
    }

    /**
     * This method is called when the NetworkInterfaceWatcher is receiving a
     * change in network connectivity has occurred. Indicating that network
     * connectivity is a non-wifi-state connection.
     */
    protected void nonWifiState() {
//        Log.i("NetworkWatcher", "network with non-wifi connecting");
        say("NetworkWatcher", "call nonWifiState");
    }

    /**
     * This method is called when the NetworkInterfaceWatcher is receiving a
     * change in network connectivity has occurred. Indicating that network
     * connectivity is a wifi-state connection.
     */
    protected void wifiState() {
//        Log.i("NetworkWatcher", "network wifi connecting");
        say("NetworkWatcher", "call wifiState");
    }

    public void pause() {
        say("NetworkWatcher", "pause");
        this.stop = true;
    }

    public void resume() {
        say("NetworkWatcher", "resume");
        this.stop = false;
    }

    public void release() {
        say("NetworkWatcher", "unregist");
        this.mHandler = null;
        this.debouncer = null;
    }

    /**
     * network connectivity changed, make sure called only once. 1 : false;
     * 0 : true
     */
    private int flags = 0xFF;

    @Override
    public void onReceive(Context context, Intent intent) {
        say("NetworkWatcher", "receive intent");
        if (stop) return;
//        onReceiveIntent(context);
        mHandler.post(new ContextRunnable(context) {
            @Override
            public void run(Context context) {
                onReceiveIntent(context);
            }
        });
    }

    abstract class ContextRunnable implements Runnable {
        Context context = null;
        public ContextRunnable(Context context) {
            this.context = context;
        }
        @Override
        public void run() {
            run(this.context);
        }
        abstract public void run(Context context);
    }

    private void onReceiveIntent(Context context) {
        if (!NetworkUtil.isNetworkHardwareAvailable(context)
                || !NetworkUtil.checkNetworkState(context)) {
            flags = 0xFF;
            say("NetworkWatcher", "network hardware invalid or disconnect");
            networkUnavailable();
        }

        if (!NetworkUtil.isNetworkHardwareAvailable(context)) {
            say("NetworkWatcher", "network hardware invalid");
            if (((flags >> 0) & 0x01) == 1) {
                flags = flags & 0xFE | 0xFE;
                networkHardwareInvalid();
            }
            say("NetworkWatcher", "ret@0");
            return;
        }
        say("NetworkWatcher", "network hardware available");
        if (((flags >> 1) & 0x01) == 1) {
            flags = flags & 0xFC | 0x01;
            networkHardwareAvailable();
        }

        if (!NetworkUtil.checkNetworkState(context)) {
            say("NetworkWatcher", "network disconnect");
            if (((flags >> 2) & 0x01) == 1) {
                flags = flags & 0xFB | 0xF8;
                networkDisconnect();
            }
            say("NetworkWatcher", "ret@1");
            return;
        }

        say("NetworkWatcher", "network connect");
        if (((flags >> 3) & 0x01) == 1) {
            flags = flags & 0xF3 | 0x04;
            networkConnect();
        }

        if (!NetworkUtil.checkWifiState(context)) {
            say("NetworkWatcher", "non-wifi state");
            if (((flags >> 4) & 0x01) == 1) {
                flags = flags & 0xEF | 0xE0;
                nonWifiState();
            }
            say("NetworkWatcher", "ret@2");
            return;
        }

        say("NetworkWatcher", "wifi state");
        if (((flags >> 5) & 0x01) == 1) {
            flags = flags & 0xCF | 0x10;
            wifiState();
        }
        say("NetworkWatcher", "ret@3");
    }

    protected void say(String who, String what) {
        if (enableLogcat) {
            Log.d(who, what);
        }
    }

    class Debouncer {
        long debounceMillis = 0L;
        int lastFlags = 0xFF;
        long lastTs = 0L;
        public Debouncer(long debounceMillis) {
            this.debounceMillis = debounceMillis;
        }

        public void post(int flags, Runnable runnable) {

        }
    }
}
