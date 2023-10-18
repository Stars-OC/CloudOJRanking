package xyz.starsoc.file

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("config") {
    val bot : Long by value()
    @ValueDescription("最高权限")
    val master : Long by value()
    @ValueDescription("启用本插件的群聊")
    val enableGroup : Set<Long> by value(mutableSetOf())
    @ValueDescription("多长时间检测一次 单位min")
    val checkTime by value(15)
    @ValueDescription("什么时候推送昨天的排行 \n必须严格按照HH:mm的格式")
    val rankingUpTime by value("07:30")
    @ValueDescription("什么时间段推送今天的排行 \n必须严格按照HH:mm的格式")
    val rankingUpTimeNow by value(mutableSetOf("09:50","13:20","18:10"))
    @ValueDescription("检测前多少名的实时动向")
    var monitorLimit by value(25)
    @ValueDescription("超过多少分开始推送")
    var scoreLimit by value(50)
    @ValueDescription("提升多少排名开始推送")
    var rankLimit by value(25)
    @ValueDescription("AC超过多少题开始推送")
    var passedLimit by value(5)
    @ValueDescription("分数提升占排行权重")
    var scoreUpPoint by value(0.8)
    @ValueDescription("排名提升占排行权重")
    var rankUpPoint by value(0.5)
    @ValueDescription("AC提升占排行权重")
    var passedUpPoint by value(5.1)
    @ValueDescription("现在的排名占排行权重")
    var rankPoint by value(1.5)
    @ValueDescription("题目网址后缀要加/")
    val url by value("")
    @ValueDescription("获取的api地址，一般来说没有修改就不用改\n这是排行榜的api地址")
    val rankingApi by value("api/core/ranking?page=%d&limit=%d")
    @ValueDescription("这是竞赛api地址")
    val contentsApi by value("api/core/contest?page=%d&limit=%d")
}