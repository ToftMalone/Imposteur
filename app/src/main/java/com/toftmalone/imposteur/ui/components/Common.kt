package com.toftmalone.imposteur.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toftmalone.imposteur.data.Avatars
import com.toftmalone.imposteur.ui.theme.BrandGradient
import com.toftmalone.imposteur.ui.theme.InkOutline
import com.toftmalone.imposteur.ui.theme.InkSurface
import com.toftmalone.imposteur.ui.theme.TextOnLight
import com.toftmalone.imposteur.ui.theme.TextPrimary
import com.toftmalone.imposteur.ui.theme.TextSecondary
import com.toftmalone.imposteur.ui.theme.avatarGradient

/** The warm orange-to-red backdrop used by the menu screens. */
@Composable
fun BrandBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(BrandGradient)) { content() }
}

/** Big pill-shaped call to action. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = Color.White,
    contentColor: Color = TextOnLight,
    leadingIcon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp)
            .clip(RoundedCornerShape(31.dp))
            .background(containerColor)
            .alpha(if (enabled) 1f else 0.4f)
            .clickable(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, tint = contentColor, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
        )
    }
}

/** Outlined pill used for the lower-priority actions. */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = Color.White.copy(alpha = 0.55f),
    contentColor: Color = Color.White,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(28.dp))
            .alpha(if (enabled) 1f else 0.4f)
            .clickable(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge, color = contentColor)
    }
}

/** Circular translucent icon button, as used for close and help in the game screens. */
@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Color.White.copy(alpha = 0.18f),
    tint: Color = Color.White,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(22.dp))
    }
}

/** Emoji avatar on its own gradient, sized for lists or for the reveal card. */
@Composable
fun AvatarBadge(
    avatar: Int,
    modifier: Modifier = Modifier,
    size: Int = 48,
    shape: Shape = CircleShape,
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(shape)
            .background(avatarGradient(avatar))
            // Applied last so a caller's click ripple stays inside the shape.
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = Avatars.emojiAt(avatar), fontSize = (size * 0.5).sp)
    }
}

/** Label with a minus / value / plus control, used for player and impostor counts. */
@Composable
fun StepperRow(
    label: String,
    value: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    canDecrement: Boolean = true,
    canIncrement: Boolean = true,
    supporting: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InkSurface)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            if (supporting != null) {
                Text(supporting, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
        StepperButton(Icons.Rounded.Remove, "Diminuer", canDecrement, onDecrement)
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(58.dp),
        )
        StepperButton(Icons.Rounded.Add, "Augmenter", canIncrement, onIncrement)
    }
}

@Composable
private fun StepperButton(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (enabled) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.04f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = description,
            tint = if (enabled) TextPrimary else TextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp),
        )
    }
}

/** Label plus switch, used throughout the settings screens. */
@Composable
fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InkSurface)
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).alpha(if (enabled) 1f else 0.45f)) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            if (supporting != null) {
                Text(supporting, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = InkOutline,
                uncheckedBorderColor = InkOutline,
            ),
        )
    }
}

/** Section heading used inside the scrolling settings lists. */
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = TextSecondary,
        modifier = modifier.padding(start = 6.dp, top = 8.dp, bottom = 4.dp),
    )
}
