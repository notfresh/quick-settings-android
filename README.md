# Quick Settings / 快捷设置

<p align="center">
  <b>一键直达安卓常用但藏得深的系统设置</b><br>
  <sub>One-tap shortcuts to Android system settings that are buried too deep</sub>
</p>

<p align="center">
  <a href="#中文">中文</a> · <a href="#english">English</a>
</p>

---

<a name="中文"></a>

## 简介

安卓系统的很多开关藏在多层菜单里 —— 改个语言要点 4 层,开黑白模式要点 5 层。

**Quick Settings**(App 名「快捷」)把这些入口收集到一个列表里,一行一点直达。

| 快捷项 | 跳转到 | 手动路径 |
| --- | --- | --- |
| **系统语言** | 设置 → 语言 | 设置 → 系统 → 语言与输入法 → 语言 |
| **黑白模式** | 设置 → 显示 | 设置 → 显示 → 护眼模式(开)→ 纸质护眼 → 纸质色彩 → 黑白 |
| **关于** | 弹窗 | App 名 / 版本号 / 项目主页 |

## 特性

- **零权限** —— 不申请任何权限,只发系统 Intent
- **纯离线** —— 无网络请求,无广告,无追踪
- **单 APK** —— 5.6 MB,无依赖 Google 服务框架
- **中英双语** —— 界面跟随系统语言
- **易扩展** —— 加一个新快捷项 = 实现一个接口 + 加一行代码

## 快速开始

```bash
git clone https://github.com/notfresh/quick-settings-android.git
cd quick-settings-android
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

要求:Android 7.0+(API 24+)。语言页直跳需要 Android 9+(API 28),更老的设备会 fallback 到系统设置根页。

## 项目结构

```
app/src/main/
├── java/app/qtoggle/
│   ├── MainActivity.java         主屏:快捷项列表
│   ├── ToggleAction.java         接口:每个快捷项一个实现类
│   ├── ToggleListAdapter.java    RecyclerView 适配器
│   ├── LanguageToggle.java       系统语言跳转
│   ├── GrayscaleToggle.java      黑白模式跳转
│   ├── AboutToggle.java          关于弹窗(版本号 + 项目主页)
│   ├── AppVersionHelper.java     版本号读取(单一来源)
│   └── PerAppLocaleReset.java    清理遗留的 per-app 语言覆盖
└── res/
    ├── layout/                   主屏 + 行布局
    ├── values/                   中文字符串 + 主题
    ├── values-en/                英文字符串
    ├── values-night/             夜间主题
    └── mipmap-*/                 launcher 图标(5 dpi)
```

## 扩展一个快捷项

三步,布局和 Adapter 都不用动:

```java
// 1. 实现接口
public class MyToggle implements ToggleAction {
    @Override public int getTitleRes()   { return R.string.my_title; }
    @Override public int getIconRes()    { return R.drawable.ic_my; }
    @Override public String getSubtitle(Context c) { return c.getString(R.string.my_sub); }
    @Override public void onClick(Activity a, Host h) { /* 干活 */ }
}

// 2. 加字符串(zh + en 两个文件都要加)
// 3. 在 MainActivity.buildDefaultToggles() 里 push
```

## 开发

- 语言:Java + AppCompat(无 Kotlin,零学习债)
- gradle 8.7 / AGP 8.1.0 / minSdk 24 / target 34
- 包名 `app.qtoggle`,applicationId 独立于仓库名

## 版本

| 版本 | 内容 |
| --- | --- |
| 1.2 | 修复「系统语言改中文后 App 仍显示英文」;App 改为跟随系统语言 |
| 1.1 | 新增「黑白模式」快捷跳转;新增「关于」弹窗 |
| 1.0 | 系统语言跳转 |

## 许可证

个人项目,未注明许可证前默认保留所有权利。

---

<a name="english"></a>

## Overview

Android buries many useful switches under several layers of menus — switching the
system language takes four taps, enabling black & white mode takes five.

**Quick Settings** collects those entry points into one list. One row, one tap,
you're there.

| Shortcut | Jumps to | Manual path |
| --- | --- | --- |
| **System language** | Settings → Languages | Settings → System → Languages & input → Languages |
| **Black & white mode** | Settings → Display | Settings → Display → Eye comfort (on) → Paper mode → Paper color → Black & white |
| **About** | Dialog | App name / version / project home |

## Features

- **Zero permissions** — only fires system intents
- **Fully offline** — no network calls, no ads, no tracking
- **Single APK** — 5.6 MB, no GMS dependency
- **Bilingual UI** — follows the system language
- **Easy to extend** — a new shortcut = one interface implementation + one line

## Quick start

```bash
git clone https://github.com/notfresh/quick-settings-android.git
cd quick-settings-android
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Requires Android 7.0+ (API 24). The direct language-page jump needs Android 9+
(API 28); on older devices it falls back to the root Settings page.

## Project layout

```
app/src/main/
├── java/app/qtoggle/
│   ├── MainActivity.java         Main screen: the shortcut list
│   ├── ToggleAction.java         Interface: one implementation per shortcut
│   ├── ToggleListAdapter.java    RecyclerView adapter
│   ├── LanguageToggle.java       System language shortcut
│   ├── GrayscaleToggle.java      Black & white mode shortcut
│   ├── AboutToggle.java          About dialog (version + project home)
│   ├── AppVersionHelper.java     Version reading (single source of truth)
│   └── PerAppLocaleReset.java    Clears a leftover per-app language override
└── res/
    ├── layout/                   Main screen + row layout
    ├── values/                   Chinese strings + theme
    ├── values-en/                English strings
    ├── values-night/             Night theme
    └── mipmap-*/                 Launcher icons (5 densities)
```

## Adding a shortcut

Three steps; neither the layout nor the adapter changes:

```java
// 1. Implement the interface
public class MyToggle implements ToggleAction {
    @Override public int getTitleRes()   { return R.string.my_title; }
    @Override public int getIconRes()    { return R.drawable.ic_my; }
    @Override public String getSubtitle(Context c) { return c.getString(R.string.my_sub); }
    @Override public void onClick(Activity a, Host h) { /* do the thing */ }
}

// 2. Add strings (both zh and en)
// 3. Push it in MainActivity.buildDefaultToggles()
```

## Development

- Java + AppCompat (no Kotlin, no learning debt)
- Gradle 8.7 / AGP 8.1.0 / minSdk 24 / target 34
- Package `app.qtoggle`; the applicationId is independent of the repo name

## Versions

| Version | Contents |
| --- | --- |
| 1.2 | Fixed "app still shows English after switching the system to Chinese"; the UI now follows the system language |
| 1.1 | Added the black & white mode shortcut and the About dialog |
| 1.0 | System language shortcut |

## License

Personal project. All rights reserved unless a license is stated.
