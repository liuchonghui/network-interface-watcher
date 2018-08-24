# network-interface-watcher
```
compile 'tools.android:network-interface-watcher:1.0.3'
```
```
// Usage:
mNetworkWatcher = new NetworkWatcher(this, true, new NetworkAvailabilityListener() {
            @Override
            public void onUnavailable() {
                Log.d("PPP", "current network unavailable");
                // TODO
            }
            @Override
            public void onAvailable(ConnectionType type) {
                Log.d("PPP", "current network state is " + type);
                if (type == ConnectionType.MOBILE) {
                    // TODO
                } else {
                    // TODO
                }
            }
        });
```
```
// Or:
mNetworkWatcher = new NetworkWatcher(this, true, new NetworkChangeListener() {
            @Override
            public void onInit(final NetworkType initType) {
                Log.d("PPP", " onInit " + initType);
                // TODO
            }

            @Override
            public void onChange(NetworkType oldType, final NetworkType newType) {
                Log.d("PPP", oldType + " change2 " + newType);
                // TODO
            }
        });
```
