package app.qtoggle;

import android.content.Context;
import android.os.Build;
import android.os.LocaleList;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

/**
 * 清理历史遗留的 per-app 语言覆盖。
 *
 * 背景:V1.0 的语言选择器用过 {@code AppCompatDelegate.setApplicationLocales("en")},
 * 这个 per-app 覆盖被写进系统设置(API 33+)或 androidx 自己的 SP(API < 33)并持久化。
 * 选择器后来被移除,但**覆盖值还留着** → 用户把系统语言改成中文后,App 界面仍是英文
 * (App 进程 locale 被 per-app 覆盖劫持,与系统语言无关)。
 *
 * 本类在 Activity 创建后(前台)调用一次,清掉覆盖 → App 回到"跟随系统语言"。
 *
 * API 33+: 直接用平台 {@link android.app.LocaleManager} 清(覆盖值就存在那儿);
 * API < 33: 只有 androidx 的 SP 存储,用 {@link AppCompatDelegate} 清。
 */
public final class PerAppLocaleReset {

    private PerAppLocaleReset() {
    }

    /**
     * 清掉残留的 per-app 语言覆盖,让 App 跟随系统语言。
     * 无覆盖时是 no-op;失败不抛(最坏维持现状,不能因为清理失败而 App 起不来)。
     *
     * @return true = 确实清了覆盖(系统会随后 recreate Activity)
     */
    public static boolean clearIfStale(Context context) {
        if (context == null) {
            return false;
        }

        // API 33+ : 平台 LocaleManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                android.app.LocaleManager localeManager =
                        (android.app.LocaleManager) context.getSystemService(Context.LOCALE_SERVICE);
                if (localeManager == null) {
                    return false;
                }
                LocaleList current = localeManager.getApplicationLocales();
                if (current == null || current.isEmpty()) {
                    return false; // 本来就跟随系统
                }
                localeManager.setApplicationLocales(LocaleList.getEmptyLocaleList());
                return true;
            } catch (Exception e) {
                // 厂商 ROM 可能限制前台 App 改 per-app locale —— 落到下面的 androidx 路径
                return clearViaAppCompat(context);
            }
        }

        return clearViaAppCompat(context);
    }

    private static boolean clearViaAppCompat(Context context) {
        try {
            LocaleListCompat current = AppCompatDelegate.getApplicationLocales();
            if (current == null || current.isEmpty()) {
                return false;
            }
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
