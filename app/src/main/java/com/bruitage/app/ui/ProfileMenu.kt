package com.bruitage.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.bruitage.app.model.SoundBoardProfile

private enum class ProfileDialogMode { NONE, CREATE, RENAME, DELETE_CONFIRM }

@Composable
fun ProfileMenu(
    profiles: List<SoundBoardProfile>,
    activeProfileId: String,
    onSwitch: (String) -> Unit,
    onCreate: (String) -> Unit,
    onRename: (String, String) -> Unit,
    onDuplicate: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var dialogMode by remember { mutableStateOf(ProfileDialogMode.NONE) }
    var textInput by remember { mutableStateOf("") }

    val activeProfile = profiles.find { it.id == activeProfileId }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { menuExpanded = true }
    ) {
        Text(activeProfile?.name ?: "Bruitage", fontWeight = FontWeight.Bold)
        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Changer de profil")
    }

    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
        profiles.forEach { profile ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = profile.name,
                        fontWeight = if (profile.id == activeProfileId) FontWeight.Bold else FontWeight.Normal
                    )
                },
                onClick = {
                    onSwitch(profile.id)
                    menuExpanded = false
                }
            )
        }

        HorizontalDivider()

        DropdownMenuItem(
            text = { Text("Nouveau profil") },
            leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null) },
            onClick = {
                textInput = ""
                dialogMode = ProfileDialogMode.CREATE
                menuExpanded = false
            }
        )
        DropdownMenuItem(
            text = { Text("Renommer") },
            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
            onClick = {
                textInput = activeProfile?.name ?: ""
                dialogMode = ProfileDialogMode.RENAME
                menuExpanded = false
            }
        )
        DropdownMenuItem(
            text = { Text("Dupliquer") },
            leadingIcon = { Icon(Icons.Filled.ContentCopy, contentDescription = null) },
            onClick = {
                onDuplicate(activeProfileId)
                menuExpanded = false
            }
        )
        if (profiles.size > 1) {
            DropdownMenuItem(
                text = { Text("Supprimer") },
                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                onClick = {
                    dialogMode = ProfileDialogMode.DELETE_CONFIRM
                    menuExpanded = false
                }
            )
        }
    }

    when (dialogMode) {
        ProfileDialogMode.CREATE -> AlertDialog(
            onDismissRequest = { dialogMode = ProfileDialogMode.NONE },
            title = { Text("Nouveau profil") },
            text = {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("Nom du profil") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCreate(textInput)
                        dialogMode = ProfileDialogMode.NONE
                    },
                    enabled = textInput.isNotBlank()
                ) { Text("Créer") }
            },
            dismissButton = {
                TextButton(onClick = { dialogMode = ProfileDialogMode.NONE }) { Text("Annuler") }
            }
        )

        ProfileDialogMode.RENAME -> AlertDialog(
            onDismissRequest = { dialogMode = ProfileDialogMode.NONE },
            title = { Text("Renommer le profil") },
            text = {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("Nom du profil") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRename(activeProfileId, textInput)
                        dialogMode = ProfileDialogMode.NONE
                    },
                    enabled = textInput.isNotBlank()
                ) { Text("Renommer") }
            },
            dismissButton = {
                TextButton(onClick = { dialogMode = ProfileDialogMode.NONE }) { Text("Annuler") }
            }
        )

        ProfileDialogMode.DELETE_CONFIRM -> AlertDialog(
            onDismissRequest = { dialogMode = ProfileDialogMode.NONE },
            title = { Text("Supprimer le profil ?") },
            text = { Text("« ${activeProfile?.name} » sera définitivement supprimé.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(activeProfileId)
                    dialogMode = ProfileDialogMode.NONE
                }) { Text("Supprimer") }
            },
            dismissButton = {
                TextButton(onClick = { dialogMode = ProfileDialogMode.NONE }) { Text("Annuler") }
            }
        )

        ProfileDialogMode.NONE -> {}
    }
}
