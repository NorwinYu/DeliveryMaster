package zy18735.example.deliveryMaster;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SetPriceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_price);

        Integer price = getIntent().getIntExtra("oldPrice", 0);
        getEditText().setText(price.toString());
    }

    public void onClickSubmit(View view) {
        Intent result = new Intent();
        result.putExtra("price", findTextById(R.id.editTextPrice));
        setResult(Activity.RESULT_OK,result);
        finish();
    }

    public String findTextById(int id) {
        final EditText field = (EditText) findViewById(id);
        return field.getText().toString();
    }

    public EditText getEditText() {
        return (EditText) findViewById(R.id.editTextPrice);
    }
}
