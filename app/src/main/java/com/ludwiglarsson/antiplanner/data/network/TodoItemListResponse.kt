package com.ludwiglarsson.antiplanner.data.network

import com.google.gson.annotations.SerializedName


data class TodoItemListResponse(
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("list")
    val list: List<TodoItemPOJO>,
    @SerializedName("status")
    val status: String
)