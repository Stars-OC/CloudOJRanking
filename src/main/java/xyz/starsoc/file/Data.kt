package xyz.starsoc.file

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import xyz.starsoc.file.Data.provideDelegate
import xyz.starsoc.`object`.ContestData
import xyz.starsoc.`object`.Ranker

object Data : AutoSavePluginData("ranker") {
    var count by value(0)
    val persons : MutableMap<String,Ranker> by value(mutableMapOf())
}

