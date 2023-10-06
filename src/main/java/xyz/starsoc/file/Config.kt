package xyz.starsoc.file

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("config") {
    val bot : Long by value(2159165283 )
    @ValueDescription("最高权限")
    val master by value(1027715998)
    val enableGroup by value(mutableSetOf("607402931"))
    val time by value(1800)
    var monitorLimit by value(25)
    var scoreLimit by value(50)
    var rankLimit by value(25)
    var passedLimit by value(5)
    val url by value("https://cloudoj.204.group/")
    val rankingApi by value("api/core/ranking?page=%d&limit=%d")
}