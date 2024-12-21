package com.liuxing.todo.ui.todo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import com.liuxing.todo.data.Todo

/**
 * Author：流星
 * DateTime：2024/12/20 11:51
 * Description：TodoItem
 */
@Composable
fun TodoItem(todo: Todo, onclick: () -> Unit, starOnClick: () -> Unit) {
    Card(
        onClick = { onclick() },
        modifier = Modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val starColor = if (todo.todoFinish) Color(0xFF9ACD32) else Color.Gray
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = "Todo star",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { starOnClick() },
                tint = starColor,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = todo.todoContent,
            )
        }
    }
}