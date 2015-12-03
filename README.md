# BiuEditText
biu，biu，一个有趣的EditText

这俩天看到屌炸天的[activate-power-mode](https://atom.io/packages/activate-power-mode)，这可能有点和之前的动画触发有些区别，新脑洞，依赖键盘输入。但BiuEditText不是在模仿activate-power-mode，而是一个更屌炸世界的键盘，哪里有卖真想买一个。

![帅](http://45.media.tumblr.com/cf210d7c444b3e4d5e5a49ebb0bf9dae/tumblr_ny0aidok9u1rc7zl1o3_250.gif)

如果喜欢这个效果，欢迎提pr，搞点有趣的。

# 直接看效果

![](http://7o4zmy.com1.z0.glb.clouddn.com/2015-11-24%2023_16_17.gif)

# Usage
### Step 1
##### Install with gradle
        dependencies {
            compile 'com.xujinyang.BiuEditText:library:1.0.0'
        }
### Step 2

    <me.james.biuedittext.BiuEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="来把"
        android:textColor="@android:color/white" />
### Step 3
```
public class MainActivity extends AppCompatActivity {
    private BiuEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (BiuEditText) findViewById(R.id.biucontainer);
    }
}

```