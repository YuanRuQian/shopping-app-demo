package yuan.lydia.shoppingappdemo.ui.screens.userAuth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun LoginFailureDialog(
    onDismissRequest: () -> Unit,
    errorMessage: String
) {
    AlertDialog(onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Oops!")
        },
        text = {
            Text(text = errorMessage)
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "OK, gotcha")
            }
        },
        icon = {
            Icon(imageVector = Icons.Default.Error, contentDescription = null)
        }
    )
}