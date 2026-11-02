package app.qtoggle;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

/**
 * 屏幕黑白模式 toggle。
 *
 * 实现路线 = **跳转系统设置→显示页**(技术上没有"直达护眼模式"的官方 Intent,
 * 护眼模式藏在"显示"二级菜单里)。从 ACTION_DISPLAY_SETTINGS 进入后用户手动:
 *   显示 → 护眼模式 → 开启 → 纸质护眼 → 纸质色彩 → 黑白
 *
 * 返回本 App 时由 onResume 兜底(用户是否真的开了灰度 = 不可查,系统无 API 反推
 * 当前色域模式; 副标题保持文案提示语,让用户自记)。
 */
public class GrayscaleToggle implements ToggleAction {

    @Override
    public int getTitleRes() {
        return R.string.toggle_grayscale_title;
    }

    @Override
    public int getIconRes() {
        return R.drawable.ic_grayscale;
    }

    @Override
    public String getSubtitle(android.content.Context context) {
        return context.getString(R.string.toggle_grayscale_subtitle);
    }

    @Override
    public void onClick(Activity activity, Host host) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_grayscale_title)
                .setMessage(R.string.dialog_grayscale_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> openDisplaySettings(activity))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private static void openDisplaySettings(Activity activity) {
        // API 1+ 就有,任何设备都接受。比 ACTION_DISPLAY_PREFERENCES 更稳
        Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
        if (intent.resolveActivity(activity.getPackageManager()) == null) {
            // 几乎不会发生 —— 显示页是 Settings app 内置入口。兜底跳根设置页
            intent.setAction(Settings.ACTION_SETTINGS);
        }
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, R.string.dialog_grayscale_no_intent, Toast.LENGTH_SHORT).show();
        }
    }
}
