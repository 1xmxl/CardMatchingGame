/*
 * 实验五 · RecyclerView 三张牌翻牌游戏应用
 * 作者：龚涛    学号：2024110509
 * 应用包名（applicationId / namespace）：com.gongtao2024110509.cardmatchinggame
 * 版权声明：本工程的源码、资源与文档均由龚涛（2024110509）独立完成。
 */
package com.gongtao2024110509.cardmatchinggame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 视图层（View）与适配器（Adapter）：派生自 RecyclerView.Adapter，
 * 负责把 Model 中每一张牌绑定到一个 item_card 布局上，并处理点击回调。
 *
 * <p>Adapter 自身不写游戏逻辑：点击后通过 OnCardClickListener 把位置交给
 * GameActivity（Controller），由 Controller 调用 Model 后通知 Adapter 刷新，
 * 形成标准的 MVC 单向数据流。</p>
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    /** 回调接口：由 Controller（GameActivity）实现 */
    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    private final LayoutInflater inflater;
    private final Context context;
    private final String[] faces;
    private final String backText;
    private final OnCardClickListener listener;
    private CardMatchingGame game;

    public CardAdapter(@NonNull Context context, @NonNull CardMatchingGame game,
                       @NonNull OnCardClickListener listener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.faces = context.getResources().getStringArray(R.array.card_faces);
        this.backText = context.getString(R.string.card_back);
        this.listener = listener;
        this.game = game;
    }

    public void setGame(@NonNull CardMatchingGame game) {
        this.game = game;
        notifyDataSetChanged();
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.bind(game.cardAtIndex(position), position);
    }

    @Override
    public int getItemCount() {
        return game.getCardCount();
    }

    /** ViewHolder：缓存子控件，并在构造时完成点击回调的注册与位置换算 */
    class CardViewHolder extends RecyclerView.ViewHolder {

        private final FrameLayout root;
        private final TextView face;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.card_root);
            face = itemView.findViewById(R.id.text_card_face);

            // 回调处理：ViewHolder 复用后位置会变化，用 getBindingAdapterPosition() 取实时位置；
            // NO_POSITION 表示条目已被移除，直接丢弃该次点击。
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCardClick(position);
                }
            });
        }

        void bind(Card card, int position) {
            if (card == null) {
                return;
            }
            if (card.isMatched()) {
                face.setText(faces[card.getGroupId() % faces.length]);
                face.setTextColor(ContextCompat.getColor(context, R.color.matched_text_color));
                root.setBackgroundResource(R.drawable.bg_card_matched);
            } else if (card.isFaceUp()) {
                face.setText(faces[card.getGroupId() % faces.length]);
                face.setTextColor(ContextCompat.getColor(context, R.color.face_text_color));
                root.setBackgroundResource(R.drawable.bg_card_front);
            } else {
                face.setText(backText);
                face.setTextColor(ContextCompat.getColor(context, R.color.text_light));
                root.setBackgroundResource(R.drawable.bg_card_back);
            }
            root.setContentDescription(
                    context.getString(R.string.card_content_description, position + 1));
            root.setAlpha(card.isMatched() ? 0.65f : 1f);
            itemView.setEnabled(!card.isMatched());
        }
    }
}
