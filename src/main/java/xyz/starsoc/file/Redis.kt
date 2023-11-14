package xyz.starsoc.file

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object Redis : AutoSavePluginConfig("redis") {
    val host by value("127.0.0.1")
    val port by value(6379)
    val username by value("")
    val password by value("")
    val contestTTL by value(60)
}