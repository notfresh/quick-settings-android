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
        // V1:只有系统语言跳转。后续添加 "夜间模式 / 默认浏览器 / 通知权限 ..." 在此 push。
        return Arrays.asList(
                new LanguageToggle()
        );
    }

    private void refreshEmptyState() {
        boolean empty = adapter.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
