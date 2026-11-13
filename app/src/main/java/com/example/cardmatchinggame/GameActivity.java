package com.example.cardmatchinggame;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 控制器（Controller）：三张牌翻牌游戏页面。
 *
 * <p>职责：读取资源配置决定牌数与列数 → 创建 Model → 装配 RecyclerView + Adapter →
 * 接收 Adapter 的点击回调并转发给 Model → 刷新视图与状态栏 → 处理延时翻回、通关对话框、
 * 重新开始与状态保存。Activity 中不写任何游戏规则，规则都在 CardMatchingGame / Card 中。</p>
 */
public class GameActivity extends AppCompatActivity implements CardAdapter.OnCardClickListener {

    /** 三张牌翻开后停留展示的时间（毫秒），便于玩家看清本次结果 */
    private static final long PEEK_DELAY_MS = 700;

    private CardMatchingGame game;
    private CardAdapter adapter;
    private TextView scoreView;
    private TextView stepsView;
    private TextView modeView;
    private int cardCount;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable settleTask = () -> {
        game.settle();
        adapter.refresh();
        updateStatusBar();
        checkGameOver();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // 竖屏 / 横屏使用不同的资源目录（values / values-land），实现不同牌数与列数
        cardCount = getResources().getInteger(R.integer.card_count);
        int span = getResources().getInteger(R.integer.grid_span);

        scoreView = findViewById(R.id.text_score);
        stepsView = findViewById(R.id.text_steps);
        modeView = findViewById(R.id.text_mode);
        modeView.setText(getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE
                ? R.string.orientation_landscape
                : R.string.orientation_portrait);

        game = new CardMatchingGame(cardCount);
        adapter = new CardAdapter(this, game, this);

        RecyclerView recyclerView = findViewById(R.id.recycler_cards);
        recyclerView.setLayoutManager(new GridLayoutManager(this, span));
        recyclerView.setAdapter(adapter);

        Button resetButton = findViewById(R.id.button_reset);
        resetButton.setOnClickListener(v -> restart());

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }
        updateStatusBar();
    }

    /** Adapter 点击回调：只转发给 Model，再统一刷新视图 */
    @Override
    public void onCardClick(int position) {
        if (game.isPending()) {
            return;
        }
        game.chooseCardAtIndex(position);
        adapter.refresh();
        updateStatusBar();
        if (game.isPending()) {
            handler.postDelayed(settleTask, PEEK_DELAY_MS);
        } else {
            checkGameOver();
        }
    }

    /** 重新开始：调用 Model 新增的 reset()，并取消尚未执行的翻回任务 */
    private void restart() {
        handler.removeCallbacks(settleTask);
        game.reset();
        adapter.setGame(game);
        updateStatusBar();
    }

    private void updateStatusBar() {
        scoreView.setText(getString(R.string.score_format, game.getScore()));
        stepsView.setText(getString(R.string.steps_format, game.getSteps()));
    }

    private void checkGameOver() {
        if (!game.isGameOver()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.game_over_title)
                .setMessage(getString(R.string.game_over_message, game.getSteps(), game.getScore()))
                .setPositiveButton(R.string.play_again, (dialog, which) -> restart())
                .setNegativeButton(R.string.back_to_main, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // 若正等待翻回，先收尾再保存，避免恢复后残留三张无法点击的正面牌
        if (game.isPending()) {
            handler.removeCallbacks(settleTask);
            game.settle();
        }
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.key_card_count), cardCount);
        outState.putInt(getString(R.string.key_score), game.getScore());
        outState.putInt(getString(R.string.key_steps), game.getSteps());

        int[] groups = new int[cardCount];
        boolean[] faceUp = new boolean[cardCount];
        boolean[] matched = new boolean[cardCount];
        for (int i = 0; i < cardCount; i++) {
            Card card = game.cardAtIndex(i);
            groups[i] = card.getGroupId();
            faceUp[i] = card.isFaceUp();
            matched[i] = card.isMatched();
        }
        outState.putIntArray(getString(R.string.key_groups), groups);
        outState.putBooleanArray(getString(R.string.key_face_up), faceUp);
        outState.putBooleanArray(getString(R.string.key_matched), matched);
    }

    /** 横竖屏切换时牌数不同，只有同一方向的重建才恢复棋盘 */
    private void restoreState(@NonNull Bundle state) {
        if (state.getInt(getString(R.string.key_card_count)) != cardCount) {
            return;
        }
        game.restore(
                state.getIntArray(getString(R.string.key_groups)),
                state.getBooleanArray(getString(R.string.key_face_up)),
                state.getBooleanArray(getString(R.string.key_matched)),
                state.getInt(getString(R.string.key_score)),
                state.getInt(getString(R.string.key_steps)));
        adapter.refresh();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(settleTask);
        super.onDestroy();
    }
}
