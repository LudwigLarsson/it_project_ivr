package com.ludwiglarsson.antiplanner.data.network

import com.google.gson.annotations.SerializedName

data class TodoItemRequest(
    @SerializedName("element")
    val element: TodoItemPOJO,
)