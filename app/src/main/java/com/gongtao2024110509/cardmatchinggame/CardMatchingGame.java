/*
 * 实验五 · RecyclerView 三张牌翻牌游戏应用
 * 作者：龚涛    学号：2024110509
 * 应用包名（applicationId / namespace）：com.gongtao2024110509.cardmatchinggame
 * 版权声明：本工程的源码、资源与文档均由龚涛（2024110509）独立完成。
 */
package com.gongtao2024110509.cardmatchinggame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 模型层（Model）：翻牌游戏逻辑（实验五：三张牌成组）。
 *
 * <p>该类不引用任何 Android API，是 MVC 中可独立复用的 Model：
 * 洗牌、翻牌、计分、判定都由它负责，Activity 只负责把点击事件转发进来并把状态显示出去。</p>
 *
 * <p>三张牌规则：连续翻开 3 张牌；若三张牌图案相同（两两 match）则三张一起消除并加分，
 * 否则扣分并在短暂展示后翻回。判定结果先挂起（pending），由 Controller 延时调用 settle()
 * 收尾，从而既能让玩家看清三张牌，又不把 Handler / 延时逻辑混进模型。</p>
 */
public class CardMatchingGame {

    /** 每组牌的张数（三张牌游戏） */
    public static final int MATCH_COUNT = 3;

    private static final int MATCH_BONUS = 12;
    private static final int MISMATCH_PENALTY = 3;
    private static final int FLIP_COST = 1;

    private final List<Card> cards = new ArrayList<>();
    private final List<Integer> chosen = new ArrayList<>();
    private final int cardCount;
    private final Random random = new Random();

    private int score;
    private int steps;
    private boolean pending;
    private boolean lastMatch;

    public CardMatchingGame(int cardCount) {
        if (cardCount <= 0 || cardCount % MATCH_COUNT != 0) {
            throw new IllegalArgumentException("cardCount must be a positive multiple of 3");
        }
        this.cardCount = cardCount;
        resetState();
    }

    /**
     * 实验五新增：重置游戏。重新洗牌、清零分数与回合数、清空选中状态。
     * 构造函数与「重新开始」按钮复用同一套发牌逻辑，避免两处代码不一致。
     */
    public void reset() {
        resetState();
    }

    /** 私有实现：避免在构造器中调用可被子类重写的 public 方法 */
    private void resetState() {
        cards.clear();
        chosen.clear();
        score = 0;
        steps = 0;
        pending = false;
        lastMatch = false;

        List<Integer> deck = new ArrayList<>();
        for (int group = 0; group < cardCount / MATCH_COUNT; group++) {
            for (int i = 0; i < MATCH_COUNT; i++) {
                deck.add(group);
            }
        }
        Collections.shuffle(deck, random);
        for (int groupId : deck) {
            cards.add(new Card(groupId));
        }
    }

    public Card cardAtIndex(int index) {
        if (index < 0 || index >= cards.size()) {
            return null;
        }
        return cards.get(index);
    }

    public int getCardCount() {
        return cardCount;
    }

    public int getScore() {
        return score;
    }

    public int getSteps() {
        return steps;
    }

    /** 是否已翻满三张、等待视图展示后收尾 */
    public boolean isPending() {
        return pending;
    }

    /**
     * 实验五修改点之二：由「翻第二张即判定」改为「翻满三张才判定」。
     * 判定完成后不立即翻回未成组的牌，而是置 pending，交给 Controller 延时调用 settle()。
     */
    public void chooseCardAtIndex(int index) {
        if (pending) {
            return;
        }
        Card card = cardAtIndex(index);
        if (card == null || card.isMatched() || card.isFaceUp()) {
            return;
        }

        card.setFaceUp(true);
        score -= FLIP_COST;
        chosen.add(index);
        if (chosen.size() < MATCH_COUNT) {
            return;
        }

        steps++;
        Card first = cardAtIndex(chosen.get(0));
        Card second = cardAtIndex(chosen.get(1));
        Card third = cardAtIndex(chosen.get(2));
        boolean matched = first.match(second) && first.match(third) && second.match(third);

        if (matched) {
            score += MATCH_BONUS;
            for (int position : chosen) {
                Card target = cardAtIndex(position);
                target.setMatched(true);
                target.setFaceUp(true);
            }
        } else {
            score -= MISMATCH_PENALTY;
        }
        lastMatch = matched;
        pending = true;
    }

    /** 视图展示完三张牌后由 Controller 调用：未成组则翻回，成组的牌保持消除态 */
    public boolean settle() {
        if (!pending) {
            return false;
        }
        if (!lastMatch) {
            for (int position : chosen) {
                cardAtIndex(position).setFaceUp(false);
            }
        }
        chosen.clear();
        pending = false;
        return true;
    }

    public boolean isGameOver() {
        for (Card card : cards) {
            if (!card.isMatched()) {
                return false;
            }
        }
        return true;
    }

    /** 供 Activity 保存 / 恢复界面状态，牌数不一致（横竖屏切换）时由 Activity 放弃恢复重新开局 */
    public void restore(int[] groups, boolean[] faceUp, boolean[] matched, int score, int steps) {
        if (groups == null || groups.length != cardCount) {
            return;
        }
        cards.clear();
        chosen.clear();
        for (int groupId : groups) {
            cards.add(new Card(groupId));
        }
        for (int i = 0; i < cardCount; i++) {
            Card card = cards.get(i);
            card.setFaceUp(faceUp != null && faceUp.length == cardCount && faceUp[i]);
            card.setMatched(matched != null && matched.length == cardCount && matched[i]);
            // 重建「已翻开但尚未判定」的选中列表，否则恢复后前两张正面牌会永远无法收尾
            if (card.isFaceUp() && !card.isMatched()) {
                chosen.add(i);
            }
        }
        this.score = score;
        this.steps = steps;
        this.pending = false;
        this.lastMatch = false;
    }
}
