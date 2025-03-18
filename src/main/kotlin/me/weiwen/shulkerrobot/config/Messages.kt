package me.weiwen.shulkerrobot.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Messages(
    @SerialName("no-one-nearby")
    val noOneNearby: String = "<gray>No one is nearby to hear you.</gray>",
)