# BiuEditText
biu，biu，一个有趣的EditText

这俩天看到屌炸天的[activate-power-mode](https://atom.io/packages/activate-power-mode)，这可能有点和之前的动画触发有些区别，新脑洞，依赖键盘输入。但BiuEditText不是在模仿activate-power-mode，而是一个更屌炸世界的键盘，哪里有卖真想买一个。

![帅](http://45.media.tumblr.com/cf210d7c444b3e4d5e5a49ebb0bf9dae/tumblr_ny0aidok9u1rc7zl1o3_250.gif)

如果喜欢这个效果，欢迎提pr，搞点有趣的。

# 直接看效果

![](http://7o4zmy.com1.z0.glb.clouddn.com/2015-11-24%2023_16_17.gif)

and

![](http://7o4zmy.com1.z0.glb.clouddn.com/2015-12-05%2017_56_35.gif)

# Usage
### Step 1
##### Install with gradle
        dependencies {
            compile 'com.xujinyang.BiuEditText:library:1.4.0'
        }
### Step 2

    <me.james.biuedittext.BiuEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="biu,biu,发射1号"
        android:textColor="@android:color/white"
        app:biu_duration="800"
        app:biu_text_color="@android:color/white"
        app:biu_text_scale="1.5"
        app:biu_type="flydown"
        app:biu_text_start_size="12sp" />
       
####attrs:

| 参数 | 类型 |含义|
|--------|--------|--------|
|   biu_duration     |   int     | 动画时长|
|biu_text_color|color|飞来飞去的文本颜色|
|biu_text_start_size|dimension|文本原来大小|
|biu_text_scale|float|文本放大倍数|
|biu_type|String|动画类型：flyDown，flyup|

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

License
--------
BiuEditText is opensource, contribution and feedback are welcomed

[Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

    Copyright 2015 Supercharge

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 
## About me
[CSDN](http://blog.csdn.net/mobilexu)

[weibo](http://weibo.com/3654795601/profile?topnav=1&wvr=6)