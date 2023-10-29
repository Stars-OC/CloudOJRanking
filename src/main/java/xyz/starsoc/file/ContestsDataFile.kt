package xyz.starsoc.file

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import xyz.starsoc.`object`.ContestData

object ContestsDataFile : AutoSavePluginData("ContestsDataFile"){
    var count by value(0)
    val contests : MutableMap<Int, ContestData> by value(mutableMapOf())
}