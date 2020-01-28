package zy18735.example.deliveryMaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getStringExtra("name")+" has finished one order just now.", Toast.LENGTH_LONG).show();
    }
}
