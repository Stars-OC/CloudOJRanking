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
    var endAt: Long? = null
    var startAt: Long? = null
    var createAt: Long? = null
    var inviteKey: String? = null
    var init : Boolean = false
}