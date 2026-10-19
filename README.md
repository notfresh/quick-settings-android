# QToggle

> 一键直达安卓常用但藏得深的系统设置。

第一个版本只挂一个开关:**系统语言**。

## 特性

| 开关 | 行为 |
| --- | --- |
| 系统语言 | 弹窗确认 → 跳转系统设置→语言页 |

- 中英界面一致
- launcher 图标 = 滑块(速读为"快捷开关")
- 单 APK 安装,不依赖 Google 服务框架

## 快速开始

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

要求:Android 7.0+(API 24+);`ACTION_LOCALE_SETTINGS` 在 API 28+ 才有,
老设备 fallback 到系统设置根页(在设置里搜"语言")。

## 项目结构

```
app/src/main
├── java/app/qtoggle
│   ├── MainActivity.java        主屏:快捷开关列表
│   ├── ToggleAction.java        通用接口:每个开关一行
│   ├── ToggleListAdapter.java   RecyclerView 适配器
│   └── LanguageToggle.java      系统语言跳转实现
└── res
    ├── layout/                  主屏 + 行布局
    ├── values/                  中文字符串 + 主题
    ├── values-en/               英文字符串
    └── mipmap-*/                launcher 图标(5 dpi)
```

新增开关 = 实现 `ToggleAction` 接口 + 在 `MainActivity.buildDefaultToggles()`
列表里 push 一个。布局 / Adapter / 主题都不动。

## 开发

- 包名:`app.qtoggle`
- gradle 8.1 / AGP 8.1.0 / minSdk 24 / target 34
- 沿用 `reading-share-android` 的工程约定(repos / wrapper / launcher mipmap 复用)

## 路线图

- 夜间模式深色主题 switch
- 通知权限 / 默认浏览器 / 默认启动器 / 防火墙 ……
- 急用啥先加啥

## 版本

- 1.0:系统语言跳转

## 许可证

个人项目,未注明许可证前默认保留所有权利。
