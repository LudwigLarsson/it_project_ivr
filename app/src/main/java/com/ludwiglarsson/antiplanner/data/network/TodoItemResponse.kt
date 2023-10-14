package com.ludwiglarsson.antiplanner.data.network

import com.google.gson.annotations.SerializedName


data class TodoItemResponse(
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("element")
    val element: TodoItemPOJO,
    @SerializedName("status")
    val status: String
)
