package cat.institutmontilivi.tasquesfirebase25.navegacio

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PunchClock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PunchClock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SwitchAccount
import androidx.compose.material.icons.outlined.Task
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable


@Serializable
object DestinacioPortada

@Serializable
object DestinacioPreferencies

@Serializable
object DestinacioInstruccions

@Serializable
object DestinacioQuantA

@Serializable
object DestinacioLogin

@Serializable
object DestinacioRegistre

@Serializable
object DestinacioPerfil

@Serializable
object DestinacioEstats


@Serializable
data class DestinacioTasques (val idUsuari:String)

@Serializable
data class DestinacioFoto (val url:String)

@Serializable
data class DestinacioTasca (val idTasca:String? = null)


@Serializable
data class DestinacioEstat(val idEstat:String? = null)

data class OpcioDrawer<T:Any>(val ruta:T, val iconaNoSeleccionada: ImageVector, val iconaSeleccionada: ImageVector, val titol:String, val mostraInsignia:Boolean = false, val IconaInsignia: ImageVector = Icons.Default.Star)

val opcionsDrawer = listOf(
    OpcioDrawer(DestinacioPortada, Icons.Outlined.Home, Icons.Filled.Home, "Portada"),
    OpcioDrawer(DestinacioPerfil, Icons.Outlined.SwitchAccount, Icons.Filled.SwitchAccount, "Perfil"),
    OpcioDrawer(DestinacioTasques(""), Icons.Outlined.Task, Icons.Filled.Task, "Tasques"),
    OpcioDrawer(DestinacioEstats, Icons.Outlined.PunchClock, Icons.Filled.PunchClock, "Estats"),
    OpcioDrawer(DestinacioPreferencies, Icons.Outlined.Settings, Icons.Filled.Settings, "Preferències"),
    OpcioDrawer(DestinacioInstruccions, Icons.Outlined.Info, Icons.Filled.Info, "Instruccions"),
    OpcioDrawer(DestinacioQuantA, Icons.AutoMirrored.Outlined.Chat, Icons.AutoMirrored.Filled.Chat, "Quant a ..."),
)