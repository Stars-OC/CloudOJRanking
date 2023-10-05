package xyz.starsoc.file

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object Message : AutoSavePluginConfig("message") {
    val rankUp by value("天哪！ %name% 在CloudOJ中又提升了 %rankUp% 名 (%oldRank% -> %newRank%↑)\n")
    val scoreUp by value("太卷了！ %name% 在CloudOJ中通过努力刷题 又获得了%scoreUp%分\n")
    val suffix by value("快来刷题吧，下一个卷王就是你 (" + Config.url + ")")
}