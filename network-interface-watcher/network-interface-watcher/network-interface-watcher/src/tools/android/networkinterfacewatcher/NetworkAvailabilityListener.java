package tools.android.networkinterfacewatcher;

public interface NetworkAvailabilityListener {
    /**
     * Network is not available. Stop connections.
     */
    void onUnavailable();

    /**
     * New network is available. Start connection.
     */
    void onAvailable(ConnectionType type);
}
