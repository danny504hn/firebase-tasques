package cat.institutmontilivi.tasquesfirebase25.ui.pantalles

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cat.institutmontilivi.tasquesfirebase25.R
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.analitiques.ManegadorAnalitiques
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.autentificacio.ManegadorAutentificacio
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.usuariActual
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Preview
@Composable
fun PreviewPerfil()
{
    PantallaPerfil(
        manegadorAnalitiques = ManegadorAnalitiques(LocalContext.current),
        manegadorAutentificacio = ManegadorAutentificacio(LocalContext.current),
        manegadorFirestore = ManegadorFirestore(),
        navegaALogin = { },
    )
}

@Composable
fun PantallaPerfil(
    manegadorAnalitiques: ManegadorAnalitiques,
    manegadorAutentificacio: ManegadorAutentificacio,
    manegadorFirestore: ManegadorFirestore,
    navegaALogin: () -> Unit,
)
{
    val usuari = manegadorAutentificacio.obtenUsuariActual()
    val ambit = rememberCoroutineScope()
    val formatData = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    manegadorAnalitiques.registraVisitaAPantalla(nomPantalla = "Perfil")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // ── Foto de perfil ──────────────────────────────────────────
        if (usuari?.photoUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(usuari.photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imatge de l'usuari",
                placeholder = painterResource(id = R.drawable.ic_perfil_incognit),
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(R.drawable.ic_perfil_incognit),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface),
                contentDescription = "Foto de perfil per defecte",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Nom ─────────────────────────────────────────────────────
        Text(
            text = usuari?.displayName ?: "Usuari sense nom",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Etiqueta de proveïdor ────────────────────────────────────
        val tipusProveidor = when {
            usuari == null -> "Sense sessió"
            usuari.isAnonymous -> "Anònim"
            usuari.providerData.any { it.providerId == "google.com" } -> "Google"
            usuari.providerData.any { it.providerId == "password" } -> "Correu i mot de pas"
            usuari.providerData.any { it.providerId == "phone" } -> "Telèfon"
            else -> "Registrat"
        }
        SuggestionChip(
            onClick = {},
            label = { Text(text = tipusProveidor, style = MaterialTheme.typography.labelLarge) },
            colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Card: Informació del compte ──────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Informació del compte",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Correu electrònic + verificació
                if (!usuari?.email.isNullOrEmpty()) {
                    FilaInformacio(
                        icon = Icons.Default.Email,
                        etiqueta = "Correu electrònic",
                        valor = usuari!!.email!!,
                        extra = if (usuari.isEmailVerified) "✓ Verificat" else "⚠ No verificat",
                        colorExtra = if (usuari.isEmailVerified)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }

                // Telèfon
                if (!usuari?.phoneNumber.isNullOrEmpty()) {
                    FilaInformacio(
                        icon = Icons.Default.Phone,
                        etiqueta = "Número de telèfon",
                        valor = usuari!!.phoneNumber!!
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }

                // Proveïdors d'autenticació (pot haver-n'hi més d'un)
                val proveidors = usuari?.providerData
                    ?.mapNotNull { nomProveidor(it.providerId).takeIf { n -> n.isNotEmpty() } }
                    ?.distinct()
                    ?.joinToString(", ")
                if (!proveidors.isNullOrEmpty()) {
                    FilaInformacio(
                        icon = Icons.Default.VpnKey,
                        etiqueta = "Proveïdors d'autenticació",
                        valor = proveidors
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }

                // Compte anònim / registrat
                FilaInformacio(
                    icon = if (usuari?.isAnonymous == true) Icons.Default.Warning else Icons.Default.CheckCircle,
                    etiqueta = "Estat del compte",
                    valor = if (usuari?.isAnonymous == true) "Usuari anònim" else "Usuari registrat",
                    colorValor = if (usuari?.isAnonymous == true)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // UID (parcialment ocult)
                if (!usuari?.uid.isNullOrEmpty()) {
                    FilaInformacio(
                        icon = Icons.Default.AccountCircle,
                        etiqueta = "Identificador (UID)",
                        valor = "…${usuari!!.uid.takeLast(16)}"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }

                // Data de creació del compte
                usuari?.metadata?.creationTimestamp?.takeIf { it > 0 }?.let { ts ->
                    FilaInformacio(
                        icon = Icons.Default.DateRange,
                        etiqueta = "Compte creat",
                        valor = formatData.format(Date(ts))
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }

                // Darrer inici de sessió
                usuari?.metadata?.lastSignInTimestamp?.takeIf { it > 0 }?.let { ts ->
                    FilaInformacio(
                        icon = Icons.Default.Schedule,
                        etiqueta = "Darrer inici de sessió",
                        valor = formatData.format(Date(ts))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Botó tancar sessió ───────────────────────────────────────
        Button(
            onClick = {
                ambit.launch {
                    manegadorAutentificacio.tancaSessio()
                    usuariActual.id = ""
                    usuariActual.nom = ""
                    usuariActual.correu = ""
                    usuariActual.usuarisHabituals = listOf()
                    navegaALogin()
                }
            },
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Tanca la sessió".uppercase())
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Components auxiliars ─────────────────────────────────────────────

@Composable
private fun FilaInformacio(
    icon: ImageVector,
    etiqueta: String,
    valor: String,
    colorValor: Color = MaterialTheme.colorScheme.onSurface,
    extra: String? = null,
    colorExtra: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = colorValor
            )
            if (extra != null) {
                Text(
                    text = extra,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorExtra
                )
            }
        }
    }
}

private fun nomProveidor(providerId: String): String = when (providerId) {
    "google.com"  -> "Google"
    "password"    -> "Correu i mot de pas"
    "phone"       -> "Telèfon"
    "anonymous"   -> "Anònim"
    "facebook.com"-> "Facebook"
    "twitter.com" -> "Twitter"
    "github.com"  -> "GitHub"
    "apple.com"   -> "Apple"
    else          -> providerId
}
