package app.qtoggle;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

/**
 * 「关于」行 —— 点开弹窗显示 app 名 + 版本号 + 项目主页。
 *
 * 结构跟 reading-share 的 SettingFragment.showAboutDialog 同款(app 名加粗 + 版本行 +
 * 末行 URL 下划线可点)。版本号来源统一走 {@link AppVersionHelper}(单一来源)。
 */
public class AboutToggle implements ToggleAction {

    private static final String PROJECT_URL = "https://github.com/notfresh/qtoggle-android";

    @Override
    public int getTitleRes() {
        return R.string.toggle_about_title;
    }

    @Override
    public int getIconRes() {
        return R.drawable.ic_info;
    }

    @Override
    public String getSubtitle(android.content.Context context) {
        return context.getString(R.string.toggle_about_subtitle);
    }

    @Override
    public void onClick(Activity activity, Host host) {
        String appName = activity.getString(R.string.app_name);
        String version = activity.getString(R.string.about_version_line,
                AppVersionHelper.versionName(activity));

        SpannableString ss = new SpannableString(appName + "\n" + version + "\n\n" + PROJECT_URL);

        // app 名加粗
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, appName.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 末行 URL 下划线 + 可点
        int linkStart = appName.length() + 1 + version.length() + 2;
        ss.setSpan(new URLSpan(PROJECT_URL), linkStart, ss.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView tv = new TextView(activity);
        tv.setText(ss);
        tv.setTextIsSelectable(true);
        tv.setPadding(48, 32, 48, 32);
        tv.setLinksClickable(true);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setGravity(Gravity.START);

        new AlertDialog.Builder(activity)
                .setView(tv)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}
