package xyz.starsoc.`object`

import kotlinx.serialization.Serializable

@Serializable
open class ContestData {
    var contestId: Int? = null
    var problemCount: Int? = null
    var languages: Int? = null
    var ended: Boolean? = null
    var started: Boolean? = null
    var contestName: String? = null
    var endAt: Int? = null
    var startAt: Int? = null
    var createAt: Int? = null
    val inviteKey: String? = null
}