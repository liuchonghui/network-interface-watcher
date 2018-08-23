package tool.networkinterfacewatcher.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tool.networkinterfacewatcher.app2.R;
import tools.android.networkinterfacewatcher.ConnectionType;
import tools.android.networkinterfacewatcher.NetworkWatcher;

public class MainActivity extends Activity {

    NetworkWatcher mNetworkWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btn1 = (Button) findViewById(R.id.btn1);
        final TextView btn1ret = (TextView) findViewById(R.id.btn1_ret);
        final Button btn2 = (Button) findViewById(R.id.btn2);
        final TextView btn2ret = (TextView) findViewById(R.id.btn2_ret);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
            }
        });

        mNetworkWatcher = new NetworkWatcher(this, true) {
            @Override
            protected void onUnavailable() {
                Log.d("PPP", "current network unavailable");
            }
            @Override
            protected void onAvailable(ConnectionType type) {
                Log.d("PPP", "current network state is " + type);
            }
        };
    }

    Activity getActivity() {
        return this;
    }

    @Override
    protected void onDestroy() {
        if (mNetworkWatcher != null) {
            mNetworkWatcher.release(this);
        }
        super.onDestroy();
    }
}