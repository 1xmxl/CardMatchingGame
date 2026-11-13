# 实验五 · RecyclerView 翻牌游戏应用（Dev 分支）

姓名：龚涛　学号：2024110509　APP 标题：**龚涛2024110509**

## 一、完成内容对照实验要求

| 要求 | 实现位置 |
| --- | --- |
| git 新建分支 Dev，在 Dev 上完成作业 | 本地 `Main` = 实验四基线，`Dev` = 实验五版本，两分支均有提交 |
| 不允许直接 push 更新 Main，用 PR 审核合并 | 见文末「GitHub 分支保护与 PR」操作（远端设置需在网页完成） |
| MainActivity 添加菜单，点击弹出关于对话框 | `MainActivity.java`、`res/menu/menu_main.xml`、`AlertDialog` |
| View：RecyclerView 实现 24 张牌，界面规整 | `activity_game.xml`、`item_card.xml`、`values/integers.xml`（竖屏 24 张 / 4 列） |
| Adapter：派生 RecyclerView.Adapter，重载方法并处理回调 | `CardAdapter.java`（`OnCardClickListener` + `getBindingAdapterPosition()`） |
| Model：CardMatchingGame 添加 reset 方法 | `CardMatchingGame.reset()`，构造与「重新开始」共用 |
| Controller：修改 Activity 使可玩游戏，横竖屏不同牌数 | `GameActivity.java` + `values/integers.xml` / `values-land/integers.xml`（横屏 30 张 / 6 列） |
| Model：三张牌游戏，仅改 chooseCardAtIndex 与 Card.match | `CardMatchingGame.chooseCardAtIndex()`、`Card.match()` |
| themes.xml 使用 Bridge 主题 | `res/values/themes.xml` |
| 布局与代码中不出现字符串，全部用字符串资源 | `res/values/strings.xml`（含对话框、菜单、提示、状态键、牌面图案） |
| APP 标题为「姓名+学号」 | `strings.xml` 的 `app_name` |

## 二、MVC 结构

```
Model      Card.java / CardMatchingGame.java        （纯 Java，不含 Android API）
View       activity_*.xml / item_card.xml /         （XML 布局 + RecyclerView）
           CardAdapter.java / strings.xml / drawable
Controller MainActivity.java / GameActivity.java   （转发事件、刷新界面、对话框、状态保存）
```

数据流：点击牌 → `CardAdapter` 回调 `onCardClick(position)` → `GameActivity` 调用
`game.chooseCardAtIndex(position)` → `adapter.refresh()` 重新绑定 → 若翻满三张，
`Handler` 延时 700ms 调用 `game.settle()` 翻回未成组的牌 → 检查是否通关。

## 三、三张牌规则（自行设计）

- 每一组 3 张图案相同；竖屏 8 组共 24 张，横屏 10 组共 30 张。
- 连续翻开 3 张牌后才判定：`first.match(second) && first.match(third) && second.match(third)`。
- 成组：3 张一起消除并 +12 分；未成组：-3 分并在展示 700ms 后翻回。
- 每翻一张 -1 分（翻牌成本），回合数以「翻满三张」计一次。
- `Card.match()` 被重新定义为「同组号 + 双方均已翻开且未被消除」，把成组条件封装进模型。
- 全部消除后弹出通关对话框，可「再来一局」或「返回首页」。

## 四、打开与运行

1. Android Studio（Hedgehog / Iguana 及以上）打开本目录，等待 Gradle Sync。
2. 运行 `app` 模块到模拟器或真机（minSdk 26 / compileSdk 35 / Java 17）。
3. 首页点「开始游戏」或右上角菜单进入游戏；右上角「关于」弹出对话框。
4. 旋转屏幕可看到牌数从 24 变为 30（列数由 4 变 6）；切换方向会按新牌数重新开局。

命令行构建：`./gradlew assembleDebug`（Windows 用 `gradlew.bat assembleDebug`）。

## 五、主要文件

```
app/src/main/java/com/example/cardmatchinggame/
  Card.java               CardMatchingGame.java      （Model）
  CardAdapter.java                                   （Adapter / View）
  MainActivity.java       GameActivity.java          （Controller）
app/src/main/res/
  layout/activity_main.xml  activity_game.xml  item_card.xml
  menu/menu_main.xml
  drawable/bg_card_back.xml  bg_card_front.xml  bg_card_matched.xml  bg_board.xml
  values/strings.xml  colors.xml  dimens.xml  integers.xml  themes.xml
  values-land/integers.xml  values-land/dimens.xml
  mipmap-anydpi-v26/ic_launcher.xml  ic_launcher_round.xml
```

## 六、GitHub 分支保护与 PR（按你的要求使用 GitHub，非 Gitee）

> 作业原文写的是 Gitee；此处按你的要求改为 GitHub。若老师严格要求 Gitee，
> 把下面的远程地址换成 Gitee 仓库即可，分支保护与 PR 流程完全一致。

1. 在 GitHub 新建空仓库，然后在本目录执行：

```bash
git remote add origin https://github.com/<你的用户名>/<仓库名>.git
git push -u origin Main
git push -u origin Dev
```

2. 仓库 → **Settings → Branches → Add branch ruleset**（或 Branch protection rule）：
   - Target branch：`Main`
   - 勾选 **Require a pull request before merging**
   - 勾选 **Restrict who can push to matching branches**（或直接勾选「不允许直接推送」）
   - 保存后，`git push origin Main` 会被拒绝，只能走 PR。

3. 仓库 → **Pull requests → New pull request**：base = `Main`，compare = `Dev`，
   填写标题与说明 → Create pull request → 审核（Review）→ **Merge pull request**，
   把 Dev 合并回 Main。此时仓库可见 `Main`、`Dev` 两个分支且 Main 由 PR 更新。

4. 后续改动继续在 `Dev` 上提交，重复第 3 步。

> 提交前可把提交邮箱改成 GitHub 账号邮箱：
> `git config user.email "<你的 GitHub 邮箱>"`。
