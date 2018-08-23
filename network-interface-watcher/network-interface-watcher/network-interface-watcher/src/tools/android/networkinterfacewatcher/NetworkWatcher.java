package tools.android.networkinterfacewatcher;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public class NetworkWatcher {
    protected NetworkInterfaceWatcher connectivityWatcher;
    protected NetworkState state;
    protected ConnectionType type;
    private Object lock = new Object();

    public NetworkWatcher(Context context) {
        this(context, false);
    }

    public NetworkWatcher(Context context, boolean enableLogcat) {
        this(context, enableLogcat, 0L);
    }

    public NetworkWatcher(Context context, long debounceMillis) {
        this(context, false, debounceMillis);
    }

    public NetworkWatcher(Context context, boolean enableLogcat, long debounceMillis) {
        connectivityWatcher = new NetworkInterfaceWatcher(enableLogcat, debounceMillis) {
            @Override
            protected void networkUnavailable() {
                super.networkUnavailable();
                NetworkType oldType = getNetType();
                state = NetworkState.unavailable;
                type = null;
                NetworkType newType = getNetType();
                synchronized (lock) {
                    onUnavailable();
                    if (oldType != newType) {
                        onChange(oldType, newType);
                    }
                }
            }

            @Override
            protected void nonWifiState() {
                super.nonWifiState();
                NetworkType oldType = getNetType();
                state = NetworkState.available;
                type = ConnectionType.MOBILE;
                NetworkType newType = getNetType();
                synchronized (lock) {
                    onAvailable(ConnectionType.MOBILE);
                    if (oldType != newType) {
                        onChange(oldType, newType);
                    }
                }
            }

            @Override
            protected void wifiState() {
                super.wifiState();
                NetworkType oldType = getNetType();
                state = NetworkState.available;
                type = ConnectionType.WIFI;
                NetworkType newType = getNetType();
                synchronized (lock) {
                    onAvailable(ConnectionType.WIFI);
                    if (oldType != newType) {
                        onChange(oldType, newType);
                    }
                }
            }

        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        try {
            context.registerReceiver(connectivityWatcher, filter);
            connectivityWatcher.say("NetworkWatcher", "regist");
        } catch (Throwable t) {
            t.printStackTrace();
        }

        state = NetworkState.unavailable;
        type = null;
        if (NetworkUtil.checkNetworkState(context)) {
            state = NetworkState.available;
            if (!NetworkUtil.checkWifiState(context)) {
                type = ConnectionType.MOBILE;
            } else {
                type = ConnectionType.WIFI;
            }
        }
    }

    public NetworkState getState() {
        return state;
    }

    public ConnectionType getType() {
        return this.type;
    }

    public NetworkType getNetType() {
        if (NetworkState.unavailable == getState()) {
            return NetworkType.Disconnect;
        } else if (NetworkState.available == getState()) {
            if (ConnectionType.MOBILE == getType()) {
                return NetworkType.Mobile;
            } else if (ConnectionType.WIFI == getType()) {
                return NetworkType.Wifi;
            }
        }
        return NetworkType.Unknown;
    }

    public void release(Context context) {
        if (connectivityWatcher != null) {
            try {
                context.unregisterReceiver(connectivityWatcher);
                connectivityWatcher.say("NetworkWatcher", "unregist");
            } catch (Exception e) {
            }
        }
        connectivityWatcher = null;
    }

    /**
     * Network is not available. Stop connections.
     */
    protected void onUnavailable() {
    }

    /**
     * New network is available. Start connection.
     */
    protected void onAvailable(final ConnectionType type) {
    }

    /**
     * Network change from 'oldType' to 'newType'
     */
    protected void onChange(final NetworkType oldType, final NetworkType newType) {
    }
}
