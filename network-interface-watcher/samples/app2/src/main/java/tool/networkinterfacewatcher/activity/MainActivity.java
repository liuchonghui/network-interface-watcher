package tool.networkinterfacewatcher.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import tool.networkinterfacewatcher.app2.R;
import tools.android.networkinterfacewatcher.NetworkChangeListener;
import tools.android.networkinterfacewatcher.NetworkType;
import tools.android.networkinterfacewatcher.NetworkWatcher;

public class MainActivity extends Activity {

    NetworkWatcher mNetworkWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewGroup layer = (ViewGroup) findViewById(R.id.layer);
        final Button btn1 = (Button) findViewById(R.id.btn1);
        final TextView btn1ret = (TextView) findViewById(R.id.btn1_ret);
        final Button btn2 = (Button) findViewById(R.id.btn2);
        final TextView btn2ret = (TextView) findViewById(R.id.btn2_ret);
        final TextView center = (TextView) findViewById(R.id.center_text);

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

        mNetworkWatcher = new NetworkWatcher(this, true, new NetworkChangeListener() {
            @Override
            public void onInit(final NetworkType initType) {
                Log.d("PPP", " onInit " + initType);
                layer.post(new Runnable() {
                    @Override
                    public void run() {
                        if (NetworkType.Disconnect == initType) {
                            layer.setBackgroundColor(Color.parseColor("#ffffff"));
                            center.setText("disconnect");
                        } else if (NetworkType.Mobile == initType) {
                            layer.setBackgroundColor(Color.parseColor("#7799ff"));
                            center.setText("mobile");
                        } else if (NetworkType.Wifi == initType) {
                            layer.setBackgroundColor(Color.parseColor("#ff9977"));
                            center.setText("wifi");
                        }
                    }
                });
            }

            @Override
            public void onChange(NetworkType oldType, final NetworkType newType) {
                Log.d("PPP", oldType + " change2 " + newType);
                layer.post(new Runnable() {
                    @Override
                    public void run() {
                        if (NetworkType.Disconnect == newType) {
                            layer.setBackgroundColor(Color.parseColor("#ffffff"));
                            center.setText("disconnect");
                        } else if (NetworkType.Mobile == newType) {
                            layer.setBackgroundColor(Color.parseColor("#7799ff"));
                            center.setText("mobile");
                        } else if (NetworkType.Wifi == newType) {
                            layer.setBackgroundColor(Color.parseColor("#ff9977"));
                            center.setText("wifi");
                        }
                    }
                });
            }
        });
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