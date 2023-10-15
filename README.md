# CloudOJRanking

## 前言
本项目是基于[Mirai](https://github.com/mamoe/mirai)框架开发的一个利用于MCL的插件   
对接于在线判题的开源项目[CloudOJ](https://github.com/ifyun/Cloud-OJ) 一个很好用的判题网站     

**高自由度** **功能齐全** 的排行榜推送插件

## 效果图

获取到成绩的推送  
![ranking](/READMEPIC/ranking.png)

日榜推送    
![dailyRanking](/READMEPIC/dailyRanking.png)

## 指令
### 分为两种指令集
一种是利用拥有权限的指令集的方法，需要引入[chat-Command](https://github.com/project-mirai/chat-command)进行使用  
权限为`xyz.starsoc.cloudojranking:*`利用`/permission add QQ(你的QQ号) xyz.starsoc.cloudojranking:*`   
进行权限管理(*当然如果你有全部权限这个可以忽略不看*)    

`/ranking reload` 重载插件的配置  

#### 这个就是另一个指令集的管理权限运用

`!ranking 排行榜` 获取今日排行榜      
`!ranking 昨日排行榜` 获取昨日排行榜

## 配置文件
主要的配置文件存在于`config\xyz.starsoc.cloudojranking`中的`config`文件
```
bot: 0
# 最高权限
master: 0
# 启用本插件的群聊
enableGroup: []
# 多长时间检测一次 单位min
checkTime: 15
# 什么时候推送昨天的排行
# 必须严格按照HH:mm的格式
rankingUpTime: '07:30'
# 什么时间段推送今天的排行
# 必须严格按照HH:mm的格式
rankingUpTimeNow: 
  - '09:50'
  - '13:20'
  - '18:10'
# 检测前多少名的实时动向
monitorLimit: 25
# 超过多少分开始推送
scoreLimit: 50
# 提升多少排名开始推送
rankLimit: 25
# AC超过多少题开始推送
passedLimit: 5
# 题目网址后缀要加/
url: ''
# 获取的api地址，一般来说没有修改就不用改
rankingApi: 'api/core/ranking?page=%d&limit=%d'
```

## 后文
如果有什么问题可以点击左上角的[Issues](https://github.com/Stars-OC/CloudOJRanking/issues)进行报告，如果看到我会及时处理    
如果有什么好的建议或者新奇的想法，欢迎提交Issues。