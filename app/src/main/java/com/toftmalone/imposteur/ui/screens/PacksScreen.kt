package com.toftmalone.imposteur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toftmalone.imposteur.data.WordPack
import com.toftmalone.imposteur.ui.components.CircleIconButton
import com.toftmalone.imposteur.ui.theme.Ink
import com.toftmalone.imposteur.ui.theme.InkSurface
import com.toftmalone.imposteur.ui.theme.SuccessGreen
import com.toftmalone.imposteur.ui.theme.SuccessGreenDim
import com.toftmalone.imposteur.ui.theme.TextPrimary
import com.toftmalone.imposteur.ui.theme.TextSecondary

/**
 * The themes grid. Tapping a tile toggles it into the draw; the pencil on a
 * custom tile opens it for editing.
 */
@Composable
fun PacksScreen(
    packs: List<WordPack>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
    onCreate: () -> Unit,
    onEdit: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedWords = packs.filter { it.id in selectedIds }.sumOf { it.words.size }

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
            CreatePackButton(onCreate)
        }

        Spacer(Modifier.height(10.dp))
        Text(
            text = "Packs",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${selectedIds.size} sélectionné${if (selectedIds.size > 1) "s" else ""} · $selectedWords mots",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(packs, key = { it.id }) { pack ->
                PackTile(
                    pack = pack,
                    selected = pack.id in selectedIds,
                    onClick = { onToggle(pack.id) },
                    onEdit = { onEdit(pack.id) },
                )
            }
        }
    }
}

@Composable
private fun CreatePackButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(SuccessGreenDim)
            .border(1.dp, SuccessGreen.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Rounded.AddCircle,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = "Créer",
            style = MaterialTheme.typography.labelLarge,
            color = SuccessGreen,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun PackTile(
    pack: WordPack,
    selected: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.92f)
            .clip(shape)
            .background(if (selected) SuccessGreenDim else InkSurface)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) SuccessGreen else Color.White.copy(alpha = 0.07f),
                shape = shape,
            )
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = pack.emoji, fontSize = 52.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                text = pack.name,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${pack.words.size} mots",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }

        SelectionDot(
            selected = selected,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp),
        )

        if (pack.isCustom) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable(onClick = onEdit),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Rounded.Edit,
                    contentDescription = "Modifier ${pack.name}",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun SelectionDot(selected: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(if (selected) SuccessGreen else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (selected) SuccessGreen else Color.White.copy(alpha = 0.25f),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(
                Icons.Rounded.Check,
                contentDescription = "Sélectionné",
                tint = Ink,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
