package com.ludwiglarsson.antiplanner.data.network

import com.google.gson.annotations.SerializedName

data class TodoItemPOJO(
    @SerializedName("id")
    val itemID: String,
    @SerializedName("text")
    val itemText: String,
    @SerializedName("importance")
    val itemPriority: Priority,
    @SerializedName("deadline")
    val deadline: Long?,
    @SerializedName("done")
    val doneFlag: Boolean,
    @SerializedName("created_at")
    val dateOfCreation: Long,
    @SerializedName("changed_at")
    val dateOfChanges: Long?,
    @SerializedName("last_updated_by")
    val deviceID: String
) {
    enum class Priority {
        @SerializedName("low")
        LOW,

        @SerializedName("basic")
        NORMAL,

        @SerializedName("important")
        HIGH
    }
}