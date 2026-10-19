package app.qtoggle;

import android.app.Activity;
import android.content.Context;

/**
 * 快捷开关条目:每行一个动作。
 *
 * 单击整行 → {@link #onClick(Activity, Host)} (弹 dialog/进设置/触发即时切换)。
 * 后续添加新开关(如"夜间模式"、"默认浏览器"、"通知权限"...)只需 implements 此接口 +
 * 在 {@link MainActivity#buildDefaultToggles()} 列表里加一项,无需改 Adapter / 布局。
 *
 * 副标题返回动态 String(而非 StringRes),因为不同 toggle 副标题组成不一:
 * LanguageToggle 需要拿 Locale 拼,Locale.getDefault() 是个动态值。
 */
public interface ToggleAction {

    /** 行主标题(stringRes) */
    int getTitleRes();

    /**
     * 行副标题(动态 string)。{@link #getTitleRes()} 是固定资源;此方法因为部分 toggle
     * 的副标题包含动态值(如当前语言 / 当前亮度),返回 String 而不是 StringRes。
     */
    String getSubtitle(Context context);

    /**
     * 行被点击时触发。Activity 用于弹 dialog / startActivity,host 用于通知列表副标题已变。
     */
    void onClick(Activity activity, Host host);

    /**
     * 宿主(MainActivity)回调用。{@link #getSubtitle(Context)} 自身读动态状态,host 只负责
     * 通知 adapter 重新计算副标题 —— 比如 Activity onResume 时拿到新 Locale 时调用。
     */
    interface Host {
        void refreshSubtitle();
    }
}
