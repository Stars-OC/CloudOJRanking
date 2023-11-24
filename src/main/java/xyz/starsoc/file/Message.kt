package xyz.starsoc.file

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object Message : AutoSavePluginConfig("message") {
    val rankUp by value("天哪！ %name% 在CloudOJ中又提升了 %rankUp% 名 (%oldRank% -> %newRank%↑)\n")
    val scoreUp by value("太卷了！ %name% 在CloudOJ中通过努力刷题 又获得了%scoreUp%分\n")
    val passedUp by value("AC！ %name% 在CloudOJ又AC了 %passedUp% 题！ TA已经AC了 %passed% 题了！\n")
    val breakMonitorLimit by value("强势入围！ %name% 在CloudOJ披荆斩棘 进入了排行榜前 " + Config.monitorLimit + " 名！")
    val suffix by value("快来刷题吧，下一个卷王就是你 (%url%)")

    val prefixRankingUp by value("==== %date% 刷题排行 ====\n")
    val rankingUp by value("NO.%rank%\n%name% 的详细数据：\nTA 提升了 %rankUp% 名 (%oldRank% -> %newRank%↑)\n获得了%scoreUp%分  AC了 %passedUp% 题")
    val suffixRankingUp by value("数据更新于%date% \n" + suffix)

    val contestsWillUp by value("==== %contestName% ====\n将于 " + Config.monitorContestTime + "min后开始，请做好准备\n语言限制：%languages%\n%startAt% - %endAt%\n%inviteKey%\n竞赛网址: (%url%contests)")
    val contestsUp by value("==== %contestName% ====\n比赛已经开始了\n语言限制：%languages%\n%startAt% - %endAt%\n%inviteKey%\n竞赛网址: (%url%contests)")
    val contestsWillDown by value("==== %contestName% ====\n将于 " + Config.monitorContestEndTime + "min后结束，请继续努力\n%inviteKey%\n竞赛网址: (%url%contests)")
    val contestsDown by value("==== %contestName% ====\n竞赛已结束了\n大家可以通过以下网址查看竞赛排行榜\n竞赛排行榜: (%url%scoreboard/%contestId%)")

    val prefixContestRanking by value("==== 竞赛 %contestName% 提升排行榜 ====\n 竞赛于 %startAt% - %endAt% 进行中")
    val contestRankUp by value("太稳了！%name% 在CloudOJ的 “%contestName%” 中又提升了 %rankUp% 名！")
    val contestScoreUp by value("太帅了！%name% 在CloudOJ的 “%contestName%” 中又获得了 %scoreUp% 分！")
    val contestPassedUp by value("太强了！%name% 在CloudOJ的 “%contestName%” 中成功 AC 了 %passedUp% 题！")
    val suffixContestRankingUp by value("数据更新于%date% \n竞赛网址: %url%contests ")
    val contestInvited by value("%contestName% 竞赛 用邀请码 %inviteKey% 邀请你加入")

    val help by value("=====CloudOJRanking 帮助=====" +
            "\n!(！)ranking 排行榜 查看今日冲分榜" +
            "\n!(！)ranking 昨日排行榜 查看昨日冲分榜")


}