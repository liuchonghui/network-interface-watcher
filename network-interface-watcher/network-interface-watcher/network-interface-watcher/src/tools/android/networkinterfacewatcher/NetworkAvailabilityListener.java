package tools.android.networkinterfacewatcher;

public interface NetworkAvailabilityListener {
    /**
     * Network is not available. Stop connections.
     */
    public void onUnavailable();

    /**
     * New network is available. Start connection.
     */
    public void onAvailable(ConnectionType type);
}
