package xyz.starsoc.file

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("config") {
    val bot : Long by value()
    @ValueDescription("最高权限")
    val master : Long by value()
    val enableGroup by value(mutableSetOf("106270290"))
    val time by value(1800)
    val url by value("https://cloudoj.204.group/")
    val rankingApi by value("api/core/ranking")
}