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
        initialize();
    }

    public NetworkWatcher(Context context, boolean enableLogcat) {
        this(context, enableLogcat, 0L);
        initialize();
    }

    public NetworkWatcher(Context context, long debounceMillis) {
        this(context, false, debounceMillis);
        initialize();
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
                    try {
                        if (mNetworkAvailabilityListener != null) {
                            mNetworkAvailabilityListener.onUnavailable();
                        } else {
                            onUnavailable();
                        }
                    } catch (Throwable t) {
                    }
                }
                if (oldType != newType) {
                    synchronized (lock) {
                        try {
                            if (mNetworkChangeListener != null) {
                                mNetworkChangeListener.onChange(oldType, newType);
                            } else {
                                onChange(oldType, newType);
                            }
                        } catch (Throwable t) {
                        }
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
                    try {
                        if (mNetworkAvailabilityListener != null) {
                            mNetworkAvailabilityListener.onAvailable(ConnectionType.MOBILE);
                        } else {
                            onAvailable(ConnectionType.MOBILE);
                        }
                    } catch (Throwable t) {
                    }
                }
                if (oldType != newType) {
                    synchronized (lock) {
                        try {
                            if (mNetworkChangeListener != null) {
                                mNetworkChangeListener.onChange(oldType, newType);
                            } else {
                                onChange(oldType, newType);
                            }
                        } catch (Throwable t) {
                        }
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
                    try {
                        if (mNetworkAvailabilityListener != null) {
                            mNetworkAvailabilityListener.onAvailable(ConnectionType.WIFI);
                        } else {
                            onAvailable(ConnectionType.WIFI);
                        }
                    } catch (Throwable t) {
                    }
                }
                synchronized (lock) {
                    if (oldType != newType) {
                        try {
                            if (mNetworkChangeListener != null) {
                                mNetworkChangeListener.onChange(oldType, newType);
                            } else {
                                onChange(oldType, newType);
                            }
                        } catch (Throwable t) {
                        }
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

    public NetworkWatcher(Context context, NetworkChangeListener listener) {
        this(context);
        setNetworkChangeListener(listener);
        lazyInitialize();
    }

    public NetworkWatcher(Context context, boolean enableLogcat, NetworkChangeListener listener) {
        this(context, enableLogcat);
        setNetworkChangeListener(listener);
        lazyInitialize();
    }

    public NetworkWatcher(Context context, long debounceMillis, NetworkChangeListener listener) {
        this(context, debounceMillis);
        setNetworkChangeListener(listener);
        lazyInitialize();
    }

    public NetworkWatcher(Context context, boolean enableLogcat, long debounceMillis, NetworkChangeListener listener) {
        this(context, enableLogcat, debounceMillis);
        setNetworkChangeListener(listener);
        lazyInitialize();
    }

    public NetworkWatcher(Context context, NetworkAvailabilityListener listener) {
        this(context);
        initialize();
        setNetworkAvailabilityListener(listener);
    }

    public NetworkWatcher(Context context, boolean enableLogcat, NetworkAvailabilityListener listener) {
        this(context, enableLogcat);
        initialize();
        setNetworkAvailabilityListener(listener);
    }

    public NetworkWatcher(Context context, long debounceMillis, NetworkAvailabilityListener listener) {
        this(context, debounceMillis);
        initialize();
        setNetworkAvailabilityListener(listener);
    }

    public NetworkWatcher(Context context, boolean enableLogcat, long debounceMillis, NetworkAvailabilityListener listener) {
        this(context, enableLogcat, debounceMillis);
        initialize();
        setNetworkAvailabilityListener(listener);
    }

    private void initialize() {
        synchronized (lock) {
            try {
                onInit(getNetType());
            } catch (Throwable t) {
            }
        }
    }

    private void lazyInitialize() {
        synchronized (lock) {
            try {
                if (mNetworkChangeListener != null) {
                    mNetworkChangeListener.onInit(getNetType());
                } else {
                    onInit(getNetType());
                }
            } catch (Throwable t) {
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

    private NetworkAvailabilityListener mNetworkAvailabilityListener;

    private void setNetworkAvailabilityListener(NetworkAvailabilityListener l) {
        this.mNetworkAvailabilityListener = l;
    }

    private NetworkChangeListener mNetworkChangeListener;

    private void setNetworkChangeListener(NetworkChangeListener l) {
        this.mNetworkChangeListener = l;
    }

    public void release(Context context) {
        if (connectivityWatcher != null) {
            try {
                context.unregisterReceiver(connectivityWatcher);
                connectivityWatcher.say("NetworkWatcher", "unregist");
            } catch (Exception e) {
            }
        }
        this.connectivityWatcher = null;
        this.mNetworkAvailabilityListener = null;
        this.mNetworkChangeListener = null;
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
     * Network type of initialize
     */
    protected void onInit(final NetworkType initType) {
    }

    /**
     * Network change from 'oldType' to 'newType'
     */
    protected void onChange(final NetworkType oldType, final NetworkType newType) {
    }
}
