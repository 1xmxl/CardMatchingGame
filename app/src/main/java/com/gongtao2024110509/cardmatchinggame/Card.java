/*
 * 实验五 · RecyclerView 三张牌翻牌游戏应用
 * 作者：龚涛    学号：2024110509
 * 应用包名（applicationId / namespace）：com.gongtao2024110509.cardmatchinggame
 * 版权声明：本工程的源码、资源与文档均由龚涛（2024110509）独立完成。
 */
package com.gongtao2024110509.cardmatchinggame;

/**
 * 模型层（Model）：一张牌。
 *
 * <p>实验五（三张牌模式）对 match() 做了重新设计：两张牌要能参与同一组三张牌的判定，
 * 除了图案（组号）相同外，还必须都处于「已翻开、未被消除」的状态。
 * 这样 CardMatchingGame.chooseCardAtIndex() 只需验证三张牌两两 match 即可完成成组判定，
 * 匹配规则完全封装在模型内部，视图层与控制器层无需改动。</p>
 */
public class Card {

    private final int groupId;
    private boolean faceUp;
    private boolean matched;

    public Card(int groupId) {
        this.groupId = groupId;
        this.faceUp = false;
        this.matched = false;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    /**
     * 三张牌模式的匹配判定（实验五修改点之一）。
     * 实验四只比较图案编号；这里增加状态约束，使「任意两张」都能作为三张成组的判定依据。
     */
    public boolean match(Card other) {
        return other != null
                && other != this
                && this.faceUp
                && other.faceUp
                && !this.matched
                && !other.matched
                && this.groupId == other.groupId;
    }
}
