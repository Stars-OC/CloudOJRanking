package xyz.starsoc.`object`

import kotlinx.serialization.Serializable

@Serializable
open class Ranker{
    var score: Double? = null
    var committed: Int? = null
    var name: String? = null
    var rank: Int? = null
    var passed: Int? = null
    var userId: String? = null
}

