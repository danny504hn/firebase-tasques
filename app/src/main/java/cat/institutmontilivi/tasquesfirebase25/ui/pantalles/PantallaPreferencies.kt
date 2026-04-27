package cat.institutmontilivi.tasquesfirebase25.ui.pantalles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.analitiques.ManegadorAnalitiques

//@Preview
@Composable
fun PantallaPreferencies (manegadorAnalitiques: ManegadorAnalitiques)
{
    manegadorAnalitiques.registraVisitaAPantalla("Preferències")
    Box(Modifier.fillMaxSize(). background(color = MaterialTheme.colorScheme.surfaceVariant))
    {
        Text(text = "Preferències",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center)
    }
}