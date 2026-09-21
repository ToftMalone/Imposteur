package com.toftmalone.imposteur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.toftmalone.imposteur.data.PackIcons
import com.toftmalone.imposteur.data.WordPack
import com.toftmalone.imposteur.ui.components.CircleIconButton
import com.toftmalone.imposteur.ui.components.PackIcon
import com.toftmalone.imposteur.ui.components.PrimaryButton
import com.toftmalone.imposteur.ui.theme.BrandOrange
import com.toftmalone.imposteur.ui.theme.ImposteurRed
import com.toftmalone.imposteur.ui.theme.Ink
import com.toftmalone.imposteur.ui.theme.InkSurface
import com.toftmalone.imposteur.ui.theme.SuccessGreen
import com.toftmalone.imposteur.ui.theme.TextPrimary
import com.toftmalone.imposteur.ui.theme.TextSecondary

/**
 * Create or edit a custom pack. A pack needs a name and at least two words
 * before it can be saved, since impostors draw a second word from the same list.
 */
@Composable
fun PackEditorScreen(
    existing: WordPack?,
    newPackId: String,
    onSave: (WordPack) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember(existing?.id) { mutableStateOf(existing?.name.orEmpty()) }
    var icon by remember(existing?.id) { mutableStateOf(existing?.icon ?: PackIcons.DEFAULT) }
    val words = remember(existing?.id) { existing?.words.orEmpty().toMutableStateList() }
    var draft by remember(existing?.id) { mutableStateOf("") }

    val canSave = name.isNotBlank() && words.size >= 2

    fun addDraft() {
        val trimmed = draft.trim()
        if (trimmed.isNotEmpty() && words.none { it.equals(trimmed, ignoreCase = true) }) {
            words.add(0, trimmed)
        }
        draft = ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Ink)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(Icons.Rounded.ArrowBack, "Retour", onBack, background = InkSurface)
            Text(
                text = if (existing == null) "Nouveau pack" else "Modifier",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
            )
            if (existing != null) {
                CircleIconButton(
                    icon = Icons.Rounded.DeleteOutline,
                    contentDescription = "Supprimer le pack",
                    onClick = { onDelete(existing.id) },
                    background = ImposteurRed.copy(alpha = 0.16f),
                    tint = ImposteurRed,
                )
            } else {
                Spacer(Modifier.size(44.dp))
            }
        }

        Spacer(Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                EditorField(
                    value = name,
                    onValueChange = { name = it.take(24) },
                    placeholder = "Nom du pack",
                    imeAction = ImeAction.Next,
                )
            }

            item {
                Text("Icône", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PackIcons.ALL) { candidate ->
                        val selected = candidate == icon
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selected) BrandOrange.copy(alpha = 0.22f) else InkSurface)
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) BrandOrange else Color.White.copy(alpha = 0.07f),
                                    shape = RoundedCornerShape(16.dp),
                                )
                                .clickable { icon = candidate },
                            contentAlignment = Alignment.Center,
                        ) {
                            PackIcon(icon = candidate, size = 36)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Mots (${words.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (words.size >= 2) TextSecondary else ImposteurRed,
                )
                Spacer(Modifier.height(8.dp))
                EditorField(
                    value = draft,
                    onValueChange = { draft = it.take(28) },
                    placeholder = "Ajouter un mot puis Entrée",
                    imeAction = ImeAction.Done,
                    onImeAction = ::addDraft,
                    trailing = {
                        if (draft.isNotBlank()) {
                            Text(
                                text = "Ajouter",
                                style = MaterialTheme.typography.labelMedium,
                                color = SuccessGreen,
                                modifier = Modifier
                                    .clickable(onClick = ::addDraft)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                            )
                        }
                    },
                )
            }

            itemsIndexed(words, key = { _, word -> word }) { _, word ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(InkSurface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = word,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.06f))
                            .clickable { words.remove(word) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Supprimer $word",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            if (words.size < 2) {
                item {
                    Text(
                        text = "Ajoute au moins 2 mots : l'imposteur reçoit un autre mot du même pack.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        PrimaryButton(
            text = "Enregistrer",
            onClick = {
                onSave(
                    WordPack(
                        id = existing?.id ?: newPackId,
                        name = name.trim(),
                        icon = icon,
                        words = words.toList(),
                        isCustom = true,
                    ),
                )
            },
            enabled = canSave,
            containerColor = SuccessGreen,
            contentColor = Ink,
        )
    }
}

@Composable
private fun EditorField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    imeAction: ImeAction,
    onImeAction: () -> Unit = {},
    trailing: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(placeholder, color = TextSecondary) },
        trailingIcon = trailing,
        textStyle = MaterialTheme.typography.titleMedium,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() },
            onNext = { onImeAction() },
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InkSurface,
            unfocusedContainerColor = InkSurface,
            focusedBorderColor = BrandOrange,
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = BrandOrange,
        ),
    )
}
