# BiuEditText
biu，biu，一个有趣的EditText

# 直接看效果

![](http://7o4zmy.com1.z0.glb.clouddn.com/biu.gif)

# Usage

```

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "main";
    private BiuEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (BiuEditText) findViewById(R.id.biucontainer);
    }
}

```