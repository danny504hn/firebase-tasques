package cat.institutmontilivi.tasquesfirebase25.ui.pantalles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cat.institutmontilivi.tasquesfirebase25.R
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.analitiques.ManegadorAnalitiques


//@Preview
@Composable
fun PantallaPortada (manegadorAnalitiques: ManegadorAnalitiques, onClick:()->Unit = {})
{
    manegadorAnalitiques.registraVisitaAPantalla("Portada")
    Column(
        Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(48.dp),
    )
    {
        Icon (
            painterResource(id = R.drawable.logomontilivi),
            contentDescription = "Montilivi",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                .padding(24.dp)
                .aspectRatio(3F),
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer (Modifier.height(48.dp))
        Divider(color = MaterialTheme.colorScheme.onSurfaceVariant,modifier= Modifier.height(15.dp))
        Spacer (Modifier.weight(1F))
        Icon(
            painterResource(id = R.drawable.tasques),
            contentDescription="Decissor",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .scale(1.5F))
        Spacer (Modifier.weight(1F))


    }
}