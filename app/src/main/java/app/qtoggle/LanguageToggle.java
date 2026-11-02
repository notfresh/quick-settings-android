package app.qtoggle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.Locale;

/**
 * 系统语言切换 toggle。
 *
 * 实现路线 = **一键直达系统语言设置页**(不调 {@code AppCompatDelegate.setApplicationLocales},
 * 那只能改本 App,不是用户要的"系统范围")。{@link Settings#ACTION_LOCALE_SETTINGS} 在 API 28+ 才有,
 * 老设备 / OEM 禁用时 fallback 到 {@link Settings#ACTION_SETTINGS} 根设置页(用户在设置里搜"语言")。
 *
 * 副标题显示当前系统语言 —— 用户从系统设置改完回 App,onResume 时 Locale 已更新,副标题会自动刷新。
 */
public class LanguageToggle implements ToggleAction {

    @Override
    public int getTitleRes() {
        return R.string.toggle_language_title;
    }

    @Override
    public int getIconRes() {
        return R.drawable.ic_language;
    }

    @Override
    public String getSubtitle(Context context) {
        // 用 Resources 的 configuration 而不是 Locale.getDefault() ——
        // 后者在 per-app locale 场景下不一定跟着当前 App 实际生效的语言走,
        // Resources 一定准(它就是 App 拿资源的 locale)。
        Locale current = resolveCurrentLocale(context);
        // 用 Locale.getDisplayLanguage(current) —— "中文" locale 下显示"中文",英文下显示"Chinese"。
        String display = current.getDisplayLanguage(current);
        if (display == null || display.isEmpty()) {
            display = current.getLanguage();
        }
        return context.getString(R.string.toggle_language_subtitle_template, display);
    }

    private static Locale resolveCurrentLocale(Context context) {
        try {
            android.content.res.Configuration config = context.getResources().getConfiguration();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                android.os.LocaleList locales = config.getLocales();
                if (locales != null && !locales.isEmpty()) {
                    return locales.get(0);
                }
            } else if (config.locale != null) {
                return config.locale;
            }
        } catch (Exception e) {
            // fallback 到默认 locale
        }
        return Locale.getDefault();
    }

    @Override
    public void onClick(Activity activity, Host host) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_language_title)
                .setMessage(R.string.dialog_language_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> openSystemLanguageSettings(activity))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private static void openSystemLanguageSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        if (intent.resolveActivity(activity.getPackageManager()) == null) {
            // 老设备 / 部分 OEM 禁用该 Intent,fallback 到根设置页
            intent.setAction(Settings.ACTION_SETTINGS);
        }
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, R.string.dialog_language_no_intent, Toast.LENGTH_SHORT).show();
        }
    }
}
