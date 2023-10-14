package com.ludwiglarsson.antiplanner

import android.content.res.ColorStateList
import android.graphics.Paint
import android.text.Spannable
import android.text.style.ImageSpan
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.antiplanner.R
import com.ludwiglarsson.antiplanner.todos.TodoItem
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@MainFragmentScope
class TodoAdapter @Inject constructor(private val callback: Callback) : RecyclerView.Adapter<TodoViewHolder>() {

    private var todoList = listOf<TodoItem>()

    fun setListTodos(todos: List<TodoItem>) {
        val diffCallback = TodoDiffCallback(todoList, todos)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        todoList = todos
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TodoViewHolder(
            layoutInflater.inflate(
                R.layout.item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val item = todoList[position]
        holder.onBind(item)
        holder.checkBox.setOnClickListener { callback.onClickCheckBox(item.itemID, !item.doneFlag) }
        holder.itemView.setOnClickListener { callback.onClickText(item.itemID) }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}

class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val textTodo: TextView = itemView.findViewById(R.id.todo)
    val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
    val date: TextView = itemView.findViewById(R.id.date_text)

    fun onBind(todoItem: TodoItem) {
        checkBox.isChecked = todoItem.doneFlag
        val imageSpan = when (todoItem.itemPriority) {
            TodoItem.Priority.HIGH -> ImageSpan(itemView.context, R.drawable.high_priority)
            TodoItem.Priority.LOW -> ImageSpan(itemView.context, R.drawable.low_priority)
            else -> null
        }
        if (imageSpan != null) {
            textTodo.paintFlags = textTodo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textTodo.text = buildSpannedString {
                append("  ")
                append(todoItem.itemText)
                this.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (todoItem.doneFlag) {
                    setSpan(StrikethroughSpan(), 2, todoItem.itemText.length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        } else {
            textTodo.text = todoItem.itemText
            if (todoItem.doneFlag) {
                textTodo.paintFlags = textTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                textTodo.paintFlags = textTodo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
        if (todoItem.doneFlag) {
            textTodo.setTextColor(ContextCompat.getColor(itemView.context, R.color.enabled))
        } else {
            textTodo.setTextColor(ContextCompat.getColor(itemView.context, R.color.dark))
        }
        if (todoItem.deadline == null) {
            date.isVisible = false
        } else {
            val formatter = SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("RU"))
            val mDate = formatter.format(todoItem.deadline)
            date.text = mDate
            date.isVisible = true
        }
        val checkBoxColor = when {
            todoItem.doneFlag -> R.color.contrast
            todoItem.itemPriority == TodoItem.Priority.HIGH -> R.color.red
            else -> R.color.contrast
        }
        checkBox.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, checkBoxColor))
    }
}

interface Callback {
    fun onClickCheckBox(id: String, isDone: Boolean)
    fun onClickText(id: String)
}

class TodoDiffCallback(
    private val oldList: List<TodoItem>,
    private val newList: List<TodoItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldTodo = oldList[oldItemPosition]
        val newTodo = newList[newItemPosition]
        return oldTodo.itemID == newTodo.itemID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldTodo = oldList[oldItemPosition]
        val newTodo = newList[newItemPosition]
        return oldTodo == newTodo
    }
}