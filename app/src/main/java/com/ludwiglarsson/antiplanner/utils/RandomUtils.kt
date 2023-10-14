package com.ludwiglarsson.antiplanner.utils

import com.ludwiglarsson.antiplanner.todos.TodoItem
import java.util.Calendar
import java.util.UUID
import kotlin.random.Random

private val random = Random(System.currentTimeMillis())
private var counter = 0
fun getRandomTodo(): TodoItem {
    val texts = listOf(
        "",
        "",
        "",
    )
    val changeDate = if (random.nextBoolean()) Calendar.getInstance().time else null
    val deadline = if (random.nextBoolean()) Calendar.getInstance().time else null
    return TodoItem(
        itemID = UUID.randomUUID().toString(),
        itemText = "${texts.random(random)} ${counter++}",
        itemPriority = TodoItem.Priority.values().random(random),
        deadline = deadline,
        doneFlag = random.nextBoolean(),
        dateOfCreation = Calendar.getInstance().time,
        dateOfChanges = changeDate,
    )
}