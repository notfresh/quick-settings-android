package app.qtoggle;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

/**
 * 快捷开关主屏。
 *
 * V1 范围只有一个开关:一键直达系统语言设置(由 {@link LanguageToggle} 提供)。
 * 后续添加其它开关只需在 {@link #buildDefaultToggles()} 列表里加一个即可,
 * Adapter / 布局 / 主题不变。
 */
public class MainActivity extends AppCompatActivity implements ToggleAction.Host {

    private RecyclerView recyclerView;
    private TextView emptyState;
    private ToggleListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.toggleRecyclerView);
        emptyState = findViewById(R.id.emptyState);

        adapter = new ToggleListAdapter(position -> {
            ToggleAction action = adapter.getItemAt(position);
            if (action != null) {
                action.onClick(this, this);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        adapter.submit(buildDefaultToggles());
        refreshEmptyState();

        // 清掉 V1.0 遗留的 per-app 语言覆盖(当时选过 English,值被持久化在系统设置里)。
        // 不清的话:系统语言改成中文后,App 进程 locale 仍被覆盖成 en → 界面一直是英文。
        if (PerAppLocaleReset.clearIfStale(this)) {
            // 已清掉,系统会 recreate Activity,此处不必继续刷新
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 从系统语言设置回来后,Locale.getDefault() 可能已变(用户改了);通知 adapter 重算副标题。
        refreshSubtitle();
    }

    @Override
    public void refreshSubtitle() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private List<ToggleAction> buildDefaultToggles() {
        // V1:系统语言跳转 + 屏幕灰度跳转。后续"夜间模式 / 默认浏览器 / 通知权限 ..."在此 push。
        return Arrays.asList(
                new LanguageToggle(),
                new GrayscaleToggle(),
                new AboutToggle()
        );
    }

    private void refreshEmptyState() {
        boolean empty = adapter.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
