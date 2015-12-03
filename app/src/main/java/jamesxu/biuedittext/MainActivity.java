package jamesxu.biuedittext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.james.biuedittext.BiuEditText;

public class MainActivity extends AppCompatActivity {
    private BiuEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (BiuEditText) findViewById(R.id.biucontainer);
    }
}
