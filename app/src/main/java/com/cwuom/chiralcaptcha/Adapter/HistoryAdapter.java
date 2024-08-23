package com.cwuom.chiralcaptcha.Adapter;

import static cn.hutool.core.img.ImgUtil.getColor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cwuom.chiralcaptcha.Dao.HistoryDao;
import com.cwuom.chiralcaptcha.Entity.EntityHistory;
import com.cwuom.chiralcaptcha.InitDataBase.InitHistoryDataBase;
import com.cwuom.chiralcaptcha.R;
import com.cwuom.chiralcaptcha.statics.Constants;
import com.cwuom.chiralcaptcha.util.Logger;
import com.cwuom.chiralcaptcha.util.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    ArrayList<EntityHistory> list;
    Context context;
    int deletePos;

    InitHistoryDataBase initHistoryDataBase;
    HistoryDao historyDao;
    HistoryAdapter.countListener countListener;
    HistoryAdapter.actionListener actionListener;

    private int maxItemsToShow = 0;

    public HistoryAdapter(ArrayList<EntityHistory> list, Context context, HistoryAdapter.countListener countListener, HistoryAdapter.actionListener actionListener, int maxItemsToShow) {
        this.list = list;
        this.context = context;
        this.countListener = countListener;
        this.actionListener = actionListener;
        initHistoryDataBase = Utils.getInstance(context);
        historyDao = initHistoryDataBase.historyDao();
        this.maxItemsToShow = maxItemsToShow;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        historyDao = initHistoryDataBase.historyDao();

        if (position == holder.getLayoutPosition()){
            EntityHistory e = list.get(holder.getLayoutPosition());
            holder.btnDelete.setOnClickListener(v -> new MaterialAlertDialogBuilder(v.getContext())
                    .setTitle("确认删除么？")
                    .setMessage("此操作不可逆。")
                    .setNeutralButton("手滑了..", null)
                    .setPositiveButton("确认删除", (dialog, which) -> {
                        deletePos = holder.getLayoutPosition();
                        deleteCard();
                        countListener.onCountChange(list.size());
                    })
                    .show());

            holder.btnReanswer.setOnClickListener(v -> new MaterialAlertDialogBuilder(v.getContext())
                    .setTitle("确定重新答题吗？")
                    .setMessage("回溯可能需要一定时间。")
                    .setNeutralButton("取消", null)
                    .setPositiveButton("前往答题", (dialog, which) -> {
                        actionListener.onAction(Constants.ACTION_REANSWER,
                                e, holder.getLayoutPosition());
                    })
                    .show());

            holder.tv_card_head_title.setText("ID:" + e.getHID());
            holder.tv_card_head_sub.setText(e.getCMolPath());
            holder.tv_card_ctime.setText(Utils.parseCTime(e.getCtime()));
            holder.tv_card_info.setText(String.format("用时: %s | 验证次数: %s",
                    list.get(holder.getLayoutPosition()).getFinishTime(), e.getCheckCount()));
            holder.tv_card_pool_index.setText(String.format("分子库: L%s", e.getCMoleculePoolIndex()));
            holder.tv_card_mol_name.setText(String.format("MOL: %s", e.cFileName));
            holder.tv_card_mol_name.setText(String.format("手性碳数量: %s", e.chiralCarbonsCount));
            if (e.getIsTimeout() == 0){
                if (e.getCheating() == 0){
                    if (e.getPassed() == 1){
                        holder.tv_card_is_passed.setText("PASSED");
                        holder.tv_card_is_passed.setTextColor(context.getColor(R.color.limegreen));
                    } else {
                        holder.tv_card_is_passed.setText("FAILED");
                        holder.tv_card_is_passed.setTextColor(context.getColor(R.color.mediumslateblue));
                    }
                } else {
                    holder.tv_card_is_passed.setText("INVALID");
                    holder.tv_card_is_passed.setTextColor(context.getColor(R.color.orangered));
                }
            } else {
                if (e.getIsTimeout() == -1){
                    holder.tv_card_is_passed.setText("UNANSWERED");
                    holder.tv_card_is_passed.setTextColor(context.getColor(R.color.grey));
                } else {
                    holder.tv_card_is_passed.setText("TIMEOUT");
                    holder.tv_card_is_passed.setTextColor(context.getColor(R.color.orangered));
                }

            }

        }
    }


    private void deleteCard() {
        notifyItemRemoved(deletePos);
        historyDao.deleteHistoryById(list.get(deletePos).getHID());
        list.remove(deletePos);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_card_head_title;
        TextView tv_card_head_sub;
        TextView tv_card_ctime;
        TextView tv_card_pool_index;
        TextView tv_card_mol_name;
        TextView tv_card_chiral_carbons_count;
        TextView tv_card_info;
        TextView tv_card_is_passed;
        Button btnDelete;
        Button btnReanswer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_card_head_title = itemView.findViewById(R.id.card_head_title);
            tv_card_head_sub = itemView.findViewById(R.id.card_head_sub);
            tv_card_ctime = itemView.findViewById(R.id.card_ctime);
            tv_card_pool_index = itemView.findViewById(R.id.card_pool_index);
            tv_card_mol_name = itemView.findViewById(R.id.card_mol_name);
            tv_card_chiral_carbons_count = itemView.findViewById(R.id.card_chiral_carbons_count);
            tv_card_info = itemView.findViewById(R.id.card_info);
            tv_card_is_passed = itemView.findViewById(R.id.card_is_passed);
            btnDelete = itemView.findViewById(R.id.del);
            btnReanswer = itemView.findViewById(R.id.reanswer);
        }
    }

    public interface countListener {
        void onCountChange(int count);
    }

    public interface actionListener {
        @SuppressLint("NotConstructor")
        void onAction(int action, EntityHistory entity, int pos);
    }


    private static class HistoryDiffCallback extends DiffUtil.Callback {
        private final List<EntityHistory> oldList;
        private final List<EntityHistory> newList;

        public HistoryDiffCallback(List<EntityHistory> oldList, List<EntityHistory> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getHID() == (newList.get(newItemPosition).getHID());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            EntityHistory oldEntity = oldList.get(oldItemPosition);
            EntityHistory newEntity = newList.get(newItemPosition);

            return oldEntity.equals(newEntity); // 假设EntityHistory重写了equals方法
        }
    }

    public void submitList(final List<EntityHistory> newList) {
        try {
            final List<EntityHistory> oldList = list;
            list = new ArrayList<>(newList.subList(0, Math.min(newList.size(), maxItemsToShow)));
            DiffUtil.calculateDiff(new HistoryDiffCallback(oldList, list), true).dispatchUpdatesTo(this);
            countListener.onCountChange(list.size());
        } catch (Exception e) {
            Logger.e("Cannot submit list to adapter: " + e.getMessage());
        }
    }


}
