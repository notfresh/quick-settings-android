package app.qtoggle;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * App 版本信息读取 —— 单一来源。
 *
 * 用 PackageManager 拿 versionName / versionCode(debug 构建默认不生成 BuildConfig,
 * PackageManager 是通用方案;参考 reading-share SettingFragment.showAboutDialog 同款)。
 */
public final class AppVersionHelper {

    private AppVersionHelper() {
    }

    /** 版本名,如 "1.1"。取不到返回 "?" */
    public static String versionName(Context context) {
        PackageInfo info = packageInfo(context);
        if (info == null || info.versionName == null) {
            return "?";
        }
        return info.versionName;
    }

    /** 版本号,如 2。取不到返回 0 */
    public static int versionCode(Context context) {
        PackageInfo info = packageInfo(context);
        if (info == null) {
            return 0;
        }
        // versionCode 在 API 28+ 废弃,新 API = getLongVersionCode()(API 28+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            return (int) info.getLongVersionCode();
        }
        return info.versionCode;
    }

    /** 展示用全串,如 "1.1 (2)" */
    public static String displayVersion(Context context) {
        return versionName(context) + " (" + versionCode(context) + ")";
    }

    private static PackageInfo packageInfo(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
