package xyz.starsoc.file

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object Message : AutoSavePluginConfig("message") {
    val rankUp by value("天哪！ %name% 在CloudOJ中又提升了 %rankUp% 名 (%oldRank% -> %newRank%↑)\n")
    val scoreUp by value("太卷了！ %name% 在CloudOJ中通过努力刷题 又获得了%scoreUp%分\n")
    val passedUp by value("AC！ %name% 在CloudOJ又AC了 %passedUp% 题！ TA已经AC了 %passed% 题了！\n")
    val breakMonitorLimit by value("强势入围！ %name% 在CloudOJ披荆斩棘 入围了前 " + Config.monitorLimit + " 名！")
    val suffix by value("快来刷题吧，下一个卷王就是你 (" + Config.url + ")")

    val prefixRankingUp by value("==== %date% 刷题排行 ====\n")
    val rankingUp by value("%name% 详细数据\nTA 昨天提升了 %rankUp% 名 (%oldRank% -> %newRank%↑)\n获得了%scoreUp%分  AC了 %passed% 题")
    val suffixRankingUp by value("数据更新于%date% \n" + suffix)


}