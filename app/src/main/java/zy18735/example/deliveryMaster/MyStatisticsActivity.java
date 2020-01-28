package zy18735.example.deliveryMaster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyStatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_statistics);

        TextView textView = (TextView) findViewById(R.id.orderNumber);
        textView.setText(String.valueOf(getIntent().getIntExtra("order", 0)));

        TextView textView1 = (TextView) findViewById(R.id.distanceCovered);
        Float d = getIntent().getFloatExtra("distance", (float) 0.0);
        textView1.setText(String.valueOf(d.intValue()));

        TextView textView2 = (TextView) findViewById(R.id.moneyEarned);
        Float m = getIntent().getFloatExtra("money", (float) 0.0);
        textView2.setText(String.valueOf(m.intValue()));
    }

    public void onClickFinish(View view) {
        finish();
    }
}
