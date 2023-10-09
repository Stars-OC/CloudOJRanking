package xyz.starsoc.file

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("config") {
    val bot : Long by value(2159165283)
    @ValueDescription("最高权限")
    val master by value(1027715998)
    val enableGroup : Set<Long> by value(mutableSetOf())
    val checkTime by value(15)
    val rankingUpTime by value("07:30")
    val rankingUpTimeNow by value(mutableSetOf("09:50","13:20","18:10"))
    var monitorLimit by value(25)
    var scoreLimit by value(50)
    var rankLimit by value(25)
    var passedLimit by value(5)
    val url by value("https://cloudoj.204.group/")
    val rankingApi by value("api/core/ranking?page=%d&limit=%d")
}