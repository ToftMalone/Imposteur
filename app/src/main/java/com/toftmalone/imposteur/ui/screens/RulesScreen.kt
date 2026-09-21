package com.toftmalone.imposteur.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toftmalone.imposteur.game.Scoring
import com.toftmalone.imposteur.ui.components.CircleIconButton
import com.toftmalone.imposteur.ui.theme.BrandOrange
import com.toftmalone.imposteur.ui.theme.CivilBlue
import com.toftmalone.imposteur.ui.theme.ImposteurRed
import com.toftmalone.imposteur.ui.theme.Ink
import com.toftmalone.imposteur.ui.theme.InkSurface
import com.toftmalone.imposteur.ui.theme.TextPrimary
import com.toftmalone.imposteur.ui.theme.TextSecondary

private data class RuleStep(val emoji: String, val title: String, val body: String)

private val STEPS = listOf(
    RuleStep(
        "📱",
        "1. Passez le téléphone",
        "Chacun son tour, un joueur prend le téléphone, découvre son rôle et glisse la carte vers le haut pour voir son mot.",
    ),
    RuleStep(
        "👥",
        "2. Civils et imposteurs",
        "Les civils partagent tous le même mot. Les imposteurs en ont un autre — ou aucun, selon le mode choisi.",
    ),
    RuleStep(
        "💬",
        "3. Décrivez sans trahir",
        "À tour de rôle, donnez un seul indice sur votre mot. Assez précis pour prouver que vous le connaissez, assez vague pour ne pas l'offrir aux imposteurs.",
    ),
    RuleStep(
        "🗳️",
        "4. Votez",
        "Débattez, puis désignez ensemble le joueur le plus suspect. Il est éliminé et son rôle est révélé.",
    ),
    RuleStep(
        "🏆",
        "5. Qui l'emporte ?",
        "Les civils gagnent si tous les imposteurs sont démasqués. Les imposteurs gagnent dès qu'ils deviennent aussi nombreux que les civils.",
    ),
    RuleStep(
        "🎯",
        "6. La dernière chance",
        "Un imposteur démasqué peut tenter de deviner le mot des civils. S'il tombe juste, il vole la victoire.",
    ),
)

/** Short, scannable house rules — reachable mid-game from the "?" button. */
@Composable
fun RulesScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(Icons.Rounded.ArrowBack, "Retour", onBack, background = InkSurface)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Comment jouer",
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "3 à 20 joueurs · un seul téléphone",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))

            STEPS.forEach { step ->
                StepCard(step)
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(8.dp))
            ScoringCard()
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun StepCard(step: RuleStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InkSurface)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(BrandOrange.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(step.emoji, fontSize = 24.sp)
        }
        Column(Modifier.padding(start = 14.dp)) {
            Text(step.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(step.body, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

@Composable
private fun ScoringCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InkSurface)
            .padding(18.dp),
    ) {
        Text("Points", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(Modifier.height(10.dp))
        ScoreLine("Victoire des civils", "+${Scoring.CIVIL_WIN_POINTS} à chaque civil", CivilBlue)
        Spacer(Modifier.height(6.dp))
        ScoreLine("Victoire des imposteurs", "+${Scoring.IMPOSTER_WIN_POINTS} à chaque imposteur", ImposteurRed)
        Spacer(Modifier.height(6.dp))
        ScoreLine("Mot deviné in extremis", "+${Scoring.IMPOSTER_STEAL_POINTS} à chaque imposteur", ImposteurRed)
    }
}

@Composable
private fun ScoreLine(label: String, value: String, accent: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        Text(value, style = MaterialTheme.typography.titleMedium, color = accent)
    }
}
