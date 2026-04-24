# BiuEditText
biu，biu，一个有趣的EditText

这俩天看到屌炸天的[activate-power-mode](https://atom.io/packages/activate-power-mode)，这可能有点和之前的动画触发有些区别，新脑洞，依赖键盘输入。但BiuEditText不是在模仿activate-power-mode，而是一个更屌炸世界的键盘，哪里有卖真想买一个。

![帅](http://45.media.tumblr.com/cf210d7c444b3e4d5e5a49ebb0bf9dae/tumblr_ny0aidok9u1rc7zl1o3_250.gif)

如果喜欢这个效果，欢迎提pr，搞点有趣的。

# Web 版本

`web/` 目录提供了一个可直接嵌入网页的 BiuEditText 控件：

![Web BiuEditText demo](web/assets/demo.gif)

```html
<link rel="stylesheet" href="./web/styles.css" />
<script src="./web/script.js"></script>

<div data-biu-input data-placeholder="Type here..."></div>
```

也可以通过 JS 手动初始化：

```js
const editor = BiuEditText.mount("#comment-box", {
    placeholder: "说点什么...",
    autoFocus: true,
    arcHeight: [180, 280]
});
```

更多接入方式见 [web/README.md](web/README.md)。

# 直接看效果

![BiuEditText demo](web/assets/demo.gif)

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
