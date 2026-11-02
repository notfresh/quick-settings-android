package app.qtoggle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import app.cash.paparazzi.DeviceConfig;
import app.cash.paparazzi.EnvironmentKt;
import app.cash.paparazzi.Paparazzi;

/**
 * Paparazzi 截图测试 —— 用 layoutlib 在 JVM 上渲染出**真截图**(无需 emulator)。
 *
 * 跑法:
 *   ./gradlew :app:recordPaparazziDebug   # 重新生成 golden PNG
 *   ./gradlew :app:verifyPaparazziDebug    # 校验当前 UI 是否与 golden 一致
 *
 * 产物:app/src/test/snapshots/images/*.png
 *
 * 实现要点(两个坑):
 * 1. RecyclerView 在 layoutlib 里会**清掉手塞进去的子 View**(它只认 adapter 数据),
 *    所以不能靠 addView 到 RecyclerView 来渲染行 —— 必须把行放进一个普通 LinearLayout。
 * 2. 也不能用 LinearLayout **替换** RecyclerView(RecyclerView 挂 FrameLayout 里,
 *    LayoutParams 类型不同 → ClassCastException)。
 *    正确做法:把整个 activity_main 布局 inflate 出来,隐藏 RecyclerView,
 *    再把行容器 addView 到 activity_main 根 LinearLayout 的末尾。
 */
public class ScreenshotTest {

    @Rule
    public Paparazzi paparazzi = new Paparazzi(
            EnvironmentKt.detectEnvironment(), DeviceConfig.PIXEL_5, "app.qtoggle");

    /** 当前 DeviceConfig —— Paparazzi 的 getter 是 Kotlin internal,Java 调不到,自己存一份 */
    private DeviceConfig deviceConfig = DeviceConfig.PIXEL_5;

    @Test
    public void mainScreen_zh() {
        switchLocale("zh");
        renderMainScreen();
    }


    @Test
    public void mainScreen_en() {
        switchLocale("en");
        renderMainScreen();
    }

    /**
     * 切 locale —— locale 是 DeviceConfig 的字段(Paparazzi 构造器第 3 参数是 appId)。
     * DeviceConfig 是 Kotlin data class + 默认参数,Java 调 copy() 要给 19 个参数,
     * 所以用反射走 copy 的完整签名,只改 locale 那一个。
     */
    private void switchLocale(String locale) {
        DeviceConfig current = deviceConfig;
        try {
            Method copy = DeviceConfig.class.getMethod("copy",
                    int.class, int.class, int.class, int.class,
                    Class.forName("com.android.resources.ScreenOrientation"),
                    Class.forName("com.android.resources.UiMode"),
                    Class.forName("com.android.resources.NightMode"),
                    Class.forName("com.android.resources.Density"),
                    float.class,
                    Class.forName("com.android.resources.LayoutDirection"),
                    String.class,
                    Class.forName("com.android.resources.ScreenRatio"),
                    Class.forName("com.android.resources.ScreenSize"),
                    Class.forName("com.android.resources.Keyboard"),
                    Class.forName("com.android.resources.TouchScreen"),
                    Class.forName("com.android.resources.KeyboardState"),
                    boolean.class,
                    Class.forName("com.android.resources.Navigation"),
                    Class.forName("com.android.resources.ScreenRound"));

            DeviceConfig updated = (DeviceConfig) copy.invoke(current,
                    current.getScreenWidth(), current.getScreenHeight(),
                    current.getXdpi(), current.getYdpi(),
                    current.getOrientation(), current.getUiMode(), current.getNightMode(),
                    current.getDensity(), current.getFontScale(),
                    current.getLayoutDirection(), locale,
                    current.getRatio(), current.getSize(), current.getKeyboard(),
                    current.getTouchScreen(), current.getKeyboardState(),
                    current.getSoftButtons(), current.getNavigation(),
                    current.getScreenRound());

            paparazzi.unsafeUpdateConfig(updated, "app.qtoggle", null);
            this.deviceConfig = updated;
        } catch (Exception e) {
            throw new RuntimeException("locale switch failed", e);
        }
    }


    private void renderMainScreen() {
        Context context = paparazzi.getContext();

        ToggleListAdapter adapter = new ToggleListAdapter(position -> { });
        adapter.submit(Arrays.asList(
                new LanguageToggle(),
                new GrayscaleToggle(),
                new AboutToggle()
        ));

        View root = paparazzi.inflate(R.layout.activity_main);

        // 隐藏 RecyclerView 容器(它 match_parent 高度,会吃掉全部空间把行挤到屏幕外)
        // + 隐藏 empty state,行内容改挂到根 LinearLayout 上
        View rv = root.findViewById(R.id.toggleRecyclerView);
        ((ViewGroup) rv.getParent()).setVisibility(View.GONE);
        root.findViewById(R.id.emptyState).setVisibility(View.GONE);

        ViewGroup rootGroup = (ViewGroup) root;
        LinearLayout rows = buildRows(context, adapter);
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootGroup.addView(rows, lp);

        paparazzi.snapshot(root);
    }

    private LinearLayout buildRows(Context context, ToggleListAdapter adapter) {
        LinearLayout rows = new LinearLayout(context);
        rows.setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 0; i < adapter.getItemCount(); i++) {
            ToggleAction action = adapter.getItemAt(i);
            View row = inflater.inflate(R.layout.item_toggle, rows, false);

            ((TextView) row.findViewById(R.id.toggleTitle)).setText(action.getTitleRes());
            ((TextView) row.findViewById(R.id.toggleSubtitle))
                    .setText(action.getSubtitle(context));
            ((ImageView) row.findViewById(R.id.toggleIcon))
                    .setImageResource(action.getIconRes());

            rows.addView(row);
        }
        return rows;
    }
}
