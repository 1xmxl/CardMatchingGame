# 实验五 · RecyclerView 三张牌翻牌游戏应用（Dev 分支）

**作者：龚涛　学号：2024110509**

- APP 标题：`龚涛2024110509`
- 应用包名（`applicationId` / `namespace`）：`com.gongtao2024110509.cardmatchinggame`
- GitHub 仓库：<https://github.com/1xmxl/CardMatchingGame>
- 署名位置：首页常驻署名、「关于」对话框、全部 Java 源文件头、`app/build.gradle`、`AndroidManifest.xml`、`strings.xml`、本 README 与实验报告

> 版权声明：本工程的源码、资源文件与实验文档均由龚涛（2024110509）独立完成。

## 一、完成内容对照实验要求

| 要求 | 实现位置 |
| --- | --- |
| git 新建分支 Dev，在 Dev 上完成作业 | `Main` = 实验四基线，`Dev` = 实验五版本，两分支均已推送到 GitHub |
| 不允许直接 push 更新 Main，用 PR 审核合并 | `Main` 已开启分支保护（要求 Pull Request、禁止 force push、管理员同样受限），合并走 Dev → Main 的 PR |
| MainActivity 添加菜单，点击弹出关于对话框 | `MainActivity.java`、`res/menu/menu_main.xml`、`AlertDialog` |
| View：RecyclerView 实现 24 张牌，界面规整 | `activity_game.xml`、`item_card.xml`、`values/integers.xml`（竖屏 24 张 / 4 列） |
| Adapter：派生 RecyclerView.Adapter，重载方法并处理回调 | `CardAdapter.java`（`OnCardClickListener` + `getBindingAdapterPosition()`） |
| Model：CardMatchingGame 添加 reset 方法 | `CardMatchingGame.reset()`，构造与「重新开始」共用 |
| Controller：修改 Activity 使可玩游戏，横竖屏不同牌数 | `GameActivity.java` + `values/integers.xml` / `values-land/integers.xml`（横屏 30 张 / 6 列） |
| Model：三张牌游戏，仅改 chooseCardAtIndex 与 Card.match | `CardMatchingGame.chooseCardAtIndex()`、`Card.match()` |
| themes.xml 使用 Bridge 主题 | `res/values/themes.xml` |
| 布局与代码中不出现字符串，全部用字符串资源 | `res/values/strings.xml`（含对话框、菜单、提示、状态键、牌面图案、署名与包名） |
| APP 标题为「姓名+学号」 | `strings.xml` 的 `app_name` |
| 明确标识作者 | 包名含姓名拼音与学号；首页、关于对话框、源码文件头、Manifest、Gradle 均标注「龚涛 / 2024110509」 |

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
2. 运行 `app` 模块到模拟器或真机（minSdk 26 / compileSdk 35 / Java 17，Gradle Wrapper 8.7）。
3. 首页点「开始游戏」或右上角菜单进入游戏；右上角「关于」弹出对话框（含作者、学号、包名）。
4. 旋转屏幕可看到牌数从 24 变为 30（列数由 4 变 6）；切换方向会按新牌数重新开局。

命令行构建：`./gradlew assembleDebug`（Windows 用 `gradlew.bat assembleDebug`）。

## 五、主要文件

```
app/src/main/java/com/gongtao2024110509/cardmatchinggame/
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

## 六、GitHub 仓库、分支保护与 PR（已完成）

仓库：<https://github.com/1xmxl/CardMatchingGame>（账号 `1xmxl`，Public）

| 项目 | 内容 |
| --- | --- |
| `Main` | 实验四基线（GridLayout + Button、两张牌配对），已设置分支保护：**Require a pull request before merging**、禁止 force push / 删除、管理员同样不可绕过 |
| `Dev` | 实验五全部成果（RecyclerView、三张牌规则、菜单与对话框、包名与作者署名） |
| PR | `Dev` → `Main`，已创建并合并；`Main` 的最终内容由 PR 更新，而非直接 push |

本地常用命令：

```bash
git remote -v                       # 查看远端
git push origin Dev                 # 后续改动继续推到 Dev
git log --oneline --decorate --all  # 查看双分支提交
```

> 作业原文要求的是 **Gitee**，此处按本人要求使用 **GitHub**，分支保护与 PR 流程完全一致。
> 若老师严格按 Gitee 检查，把 remote 换成 Gitee 仓库地址、同样推送 `Main` / `Dev`、在 Gitee 设置分支保护并发起 PR 即可，工程代码不受影响。
