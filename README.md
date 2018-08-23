# network-interface-watcher
```
compile 'tools.android:network-interface-watcher:1.0.1'
```
```
// Usage:
NetworkWatcher mNetworkWatcher = new NetworkWatcher(this, true) {
            @Override
            protected void onUnavailable() {
                Log.d("PPP", "current network unavailable");
                // TODO
            }
            @Override
            protected void onAvailable(ConnectionType type) {
                Log.d("PPP", "current network state is " + type);
                if (type == ConnectionType.MOBILE) {
                    // TODO
                } else {
                    // TODO
                }
            }
        };
```
```
// Or:
NetworkWatcher mNetworkWatcher = new NetworkWatcher(this, true) {
            @Override
            protected void onChange(NetworkType oldType, final NetworkType newType) {
                Log.d("PPP", oldType + " change2 " + newType);
                // TODO
            }
        };
```
