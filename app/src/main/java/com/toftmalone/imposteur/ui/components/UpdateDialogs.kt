package com.toftmalone.imposteur.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.toftmalone.imposteur.data.AvailableUpdate
import com.toftmalone.imposteur.data.NotesBlock
import com.toftmalone.imposteur.data.ReleaseNotes
import com.toftmalone.imposteur.data.UpdateDownload
import com.toftmalone.imposteur.data.WhatsNew
import com.toftmalone.imposteur.data.formatMegabytes
import com.toftmalone.imposteur.ui.theme.BrandGradient
import com.toftmalone.imposteur.ui.theme.BrandOrange
import com.toftmalone.imposteur.ui.theme.BrandRed
import com.toftmalone.imposteur.ui.theme.ImposteurRed
import com.toftmalone.imposteur.ui.theme.InkOutline
import com.toftmalone.imposteur.ui.theme.InkSurface
import com.toftmalone.imposteur.ui.theme.InkSurfaceHigh
import com.toftmalone.imposteur.ui.theme.SuccessGreen
import com.toftmalone.imposteur.ui.theme.TextPrimary
import com.toftmalone.imposteur.ui.theme.TextSecondary

/**
 * "Nouvelle version !": what the release brings, then the download and the
 * hand-off to Android's installer, all without leaving the app.
 */
@Composable
fun UpdateDialog(
    update: AvailableUpdate,
    currentVersion: String,
    download: UpdateDownload,
    canInstallPackages: Boolean,
    onDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    onInstall: () -> Unit,
    onLater: () -> Unit,
    onOpenReleasePage: () -> Unit,
) {
    val notes = remember(update.notes) { ReleaseNotes.forApp(update.notes) }
    val size = update.apk?.sizeBytes?.takeIf { it > 0 }?.let { " · ${formatMegabytes(it)}" }.orEmpty()
    val downloading = download is UpdateDownload.Running

    NotesWindow(
        icon = Icons.Rounded.NewReleases,
        title = "Nouvelle version !",
        subtitle = "Imposteur ${update.versionName}$size\nTu as la version $currentVersion",
        notes = notes,
        // A running download is stopped with "Annuler", never by a stray tap.
        dismissible = !downloading,
        onDismiss = onLater,
    ) {
        when (download) {
            is UpdateDownload.Running -> {
                val fraction = download.fraction
                if (fraction != null) {
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                        color = SuccessGreen,
                        trackColor = InkSurfaceHigh,
                        strokeCap = StrokeCap.Round,
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                        color = SuccessGreen,
                        trackColor = InkSurfaceHigh,
                        strokeCap = StrokeCap.Round,
                    )
                }
                Spacer(Modifier.height(10.dp))
                val total = download.totalBytes.takeIf { it > 0 }?.let { " sur ${formatMegabytes(it)}" }.orEmpty()
                FooterText("Téléchargement… ${formatMegabytes(download.downloadedBytes)}$total")
                Spacer(Modifier.height(6.dp))
                QuietAction("Annuler", onCancelDownload)
            }

            is UpdateDownload.Ready -> {
                PrimaryButton(
                    text = "Installer",
                    onClick = onInstall,
                    containerColor = SuccessGreen,
                    contentColor = Color.White,
                    leadingIcon = Icons.Rounded.SystemUpdate,
                )
                Spacer(Modifier.height(10.dp))
                FooterText(
                    if (canInstallPackages) {
                        "Android va te demander de confirmer l'installation."
                    } else {
                        "La première fois, Android te demandera d'autoriser Imposteur à installer " +
                            "des applications, puis de confirmer."
                    },
                )
                Spacer(Modifier.height(6.dp))
                QuietAction("Plus tard", onLater)
            }

            is UpdateDownload.Failed, UpdateDownload.Idle -> {
                if (download is UpdateDownload.Failed) {
                    FooterText(download.reason, color = ImposteurRed)
                    Spacer(Modifier.height(10.dp))
                }
                if (update.apk != null) {
                    PrimaryButton(
                        text = if (download is UpdateDownload.Failed) "Réessayer" else "Mettre à jour",
                        onClick = onDownload,
                        containerColor = BrandRed,
                        contentColor = Color.White,
                        leadingIcon = Icons.Rounded.FileDownload,
                    )
                } else {
                    // Only for a release published without its APK: nothing to install here.
                    FooterText("Cette version n'a pas de fichier d'installation.")
                    Spacer(Modifier.height(10.dp))
                    PrimaryButton(
                        text = "Voir la version",
                        onClick = onOpenReleasePage,
                        containerColor = BrandRed,
                        contentColor = Color.White,
                    )
                }
                Spacer(Modifier.height(6.dp))
                QuietAction("Plus tard", onLater)
            }
        }
    }
}

/** "Quoi de neuf ?": the notes of the version just installed. */
@Composable
fun WhatsNewDialog(
    whatsNew: WhatsNew,
    onClose: () -> Unit,
) {
    NotesWindow(
        icon = Icons.Rounded.AutoAwesome,
        title = "Quoi de neuf ?",
        subtitle = "Imposteur ${whatsNew.versionName}",
        notes = whatsNew.notes,
        dismissible = true,
        onDismiss = onClose,
    ) {
        PrimaryButton(
            text = "C'est parti",
            onClick = onClose,
            containerColor = BrandRed,
            contentColor = Color.White,
        )
    }
}

/** The shared frame: header, scrolling notes, then the actions. */
@Composable
private fun NotesWindow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    notes: List<NotesBlock>,
    dismissible: Boolean,
    onDismiss: () -> Unit,
    footer: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(28.dp)
    val maxHeight = (LocalConfiguration.current.screenHeightDp * 0.85f).dp

    Dialog(
        onDismissRequest = { if (dismissible) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = dismissible,
            dismissOnClickOutside = dismissible,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .heightIn(max = maxHeight)
                .clip(shape)
                .background(InkSurface)
                .border(1.dp, InkOutline, shape)
                .padding(22.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(BrandGradient),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Column(modifier = Modifier.padding(start = 14.dp)) {
                    Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = "NOUVEAUTÉS",
                style = MaterialTheme.typography.labelMedium,
                color = BrandOrange,
            )
            Spacer(Modifier.height(4.dp))

            // Takes what is left between header and actions, and scrolls if the notes are long.
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
            ) {
                if (notes.isEmpty()) {
                    Text(
                        text = "Pas de détails pour cette version.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                notes.forEach { block -> NotesBlockView(block) }
            }

            Spacer(Modifier.height(20.dp))
            footer()
        }
    }
}

@Composable
private fun NotesBlockView(block: NotesBlock) {
    when (block) {
        is NotesBlock.Heading -> Text(
            text = styled(block.text),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier.padding(top = 14.dp, bottom = 2.dp),
        )

        is NotesBlock.Paragraph -> Text(
            text = styled(block.text),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp),
        )

        is NotesBlock.Bullet -> Row(modifier = Modifier.padding(top = 6.dp)) {
            Text(
                text = block.marker,
                style = MaterialTheme.typography.bodyMedium,
                color = BrandOrange,
                modifier = Modifier.width(22.dp),
            )
            Text(
                text = styled(block.text),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }
    }
}

/** **Bold** stands out in white; `code` gets a small chip-like background. */
private fun styled(text: String): AnnotatedString = buildAnnotatedString {
    ReleaseNotes.spans(text).forEach { span ->
        when {
            span.bold -> withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextPrimary)) { append(span.text) }
            span.code -> withStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = InkSurfaceHigh)) {
                append(span.text)
            }
            else -> append(span.text)
        }
    }
}

@Composable
private fun FooterText(text: String, color: Color = TextSecondary) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun QuietAction(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = TextSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
    )
}
