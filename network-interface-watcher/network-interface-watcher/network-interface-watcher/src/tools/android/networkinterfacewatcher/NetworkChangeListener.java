package tools.android.networkinterfacewatcher;

public interface NetworkChangeListener {
    /**
     * Network type of initialize
     */
    void onInit(NetworkType initType);

    /**
     * Network change from 'oldType' to 'newType'
     */
    void onChange(NetworkType oldType, NetworkType newType);
}
