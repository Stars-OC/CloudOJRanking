package xyz.starsoc.file

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import xyz.starsoc.`object`.ContestData

object Contests : AutoSavePluginData("Contests"){
    var count by value(0)
    val contests : MutableMap<String, ContestData> by value(mutableMapOf())
}