# BiuEditText
biu，biu，一个有趣的EditText

这俩天看到屌炸天的[activate-power-mode](https://atom.io/packages/activate-power-mode)，这可能有点和之前的动画触发有些区别，新脑洞，依赖键盘输入。但BiuEditText不是在模仿activate-power-mode，而是一个更屌炸世界的键盘，哪里有卖真想买一个。

![帅](http://45.media.tumblr.com/cf210d7c444b3e4d5e5a49ebb0bf9dae/tumblr_ny0aidok9u1rc7zl1o3_250.gif)

如果喜欢这个效果，欢迎提pr，搞点有趣的。

# Web 版本

`web/` 目录提供了一个可直接嵌入网页的 BiuEditText 控件。输入字母或中文时，字符会从页面底部跳出，先飞到高于光标的位置，再沿抛物线掉落到光标处。

## 快速接入

引入样式和脚本：

```html
<link rel="stylesheet" href="./web/styles.css" />
<script src="./web/script.js"></script>
```

在页面里放一个容器即可：

```html
<div data-biu-input data-placeholder="Type here..."></div>
```

脚本会自动初始化所有带 `data-biu-input` 的元素，并创建内部编辑器和动画 Canvas。

## 手动初始化

如果你想在 JS 中控制初始化：

```html
<div id="comment-box"></div>

<script src="./web/script.js"></script>
<script>
  const editor = BiuEditText.mount("#comment-box", {
    placeholder: "说点什么...",
    autoFocus: true,
    arcHeight: [180, 280],
  });

  editor.setValue("Hello");
  console.log(editor.getValue());
</script>
```

## 配置项

```js
BiuEditText.mount("#target", {
  ariaLabel: "Biu input",
  autoFocus: false,
  placeholder: "Type here...",
  palette: ["#ffffff", "#7cf8ff", "#b9ff8b", "#ffd36a", "#ff7b9b"],
  arcHeight: [170, 260],
  maxFliers: 120,
  maxParticles: 180,
});
```

## 实例方法

```js
const editor = BiuEditText.mount("#target");

editor.focus();
editor.getValue();
editor.setValue("新的内容");
editor.destroy();
```

## 实现拆解

1. 输入本体使用 `contenteditable`，保持真实文本编辑能力。
2. 视觉层使用全页面 `position: fixed` 的透明 `canvas`，设置 `pointer-events: none`，不拦截输入。
3. 在 `beforeinput` 中拦截普通字符输入，先在光标处插入透明占位字符，占住最终落点。
4. 读取占位字符的 `getBoundingClientRect()` 作为目标位置，从页面底部生成同样的飞行字符。
5. 先计算一个高于光标的最高点 `apexY`，再根据 `startY -> apexY -> targetY` 反推初速度和飞行时长，让字符一定越过光标高度后再下落。
6. 抵达后隐藏的占位字符显形，并触发短暂的落地发光反馈。
7. 中文输入通过 `compositionstart` / `compositionend` 处理：候选词确认后删除浏览器直接插入的文本，再逐字触发落字动画。

## 运行 Demo

直接打开 `index.html` 或 `web/index.html` 即可运行。

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
