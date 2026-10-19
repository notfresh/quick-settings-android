package app.qtoggle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 快捷开关列表适配器。
 *
 * 主真相源在外部传入的 {@link #toggles} 列表本身 —— item 状态由
 * ToggleAction 的 dynamic subtitle getter 实时计算,adapter 只渲染、不持有副本,
 * 滑动复用因此不会乱(参考 reading-share §25 同款模式)。
 */
public class ToggleListAdapter extends RecyclerView.Adapter<ToggleListAdapter.VH> {

    public interface OnToggleClickListener {
        void onToggleClicked(int position);
    }

    private final List<ToggleAction> toggles = new ArrayList<>();
    private final OnToggleClickListener clickListener;

    public ToggleListAdapter(OnToggleClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void submit(List<ToggleAction> items) {
        toggles.clear();
        toggles.addAll(items);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return toggles.isEmpty();
    }

    public ToggleAction getItemAt(int position) {
        return toggles.get(position);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_toggle, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final ToggleAction action = toggles.get(position);
        holder.title.setText(action.getTitleRes());
        holder.subtitle.setText(action.getSubtitle(holder.itemView.getContext()));
        holder.itemView.setOnClickListener(v -> clickListener.onToggleClicked(position));
    }

    @Override
    public int getItemCount() {
        return toggles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;
        final ImageView icon;
        final ImageView chevron;

        VH(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.toggleTitle);
            subtitle = itemView.findViewById(R.id.toggleSubtitle);
            icon = itemView.findViewById(R.id.toggleIcon);
            chevron = itemView.findViewById(R.id.toggleChevron);
        }
    }
}
