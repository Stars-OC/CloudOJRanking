package xyz.starsoc.`object`

import kotlinx.serialization.Serializable

@Serializable
open class Ranker{
    val uid :Int? = null
    var score: Double? = null
    var committed: Int? = null
    var nickname: String? = null
    var rank: Int? = null
    var passed: Int? = null
    var username: String? = null
    val hasAvatar: Boolean = false
}

