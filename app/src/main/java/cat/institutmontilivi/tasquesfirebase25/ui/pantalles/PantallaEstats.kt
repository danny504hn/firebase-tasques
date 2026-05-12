package cat.institutmontilivi.tasquesfirebase25.ui.pantalles


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoDisturb
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.DoDisturb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.institutmontilivi.tasquesfirebase25.model.app.Estat
import cat.institutmontilivi.tasquesfirebase25.ui.viewmodels.ViewModelEstats
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController


@Preview
@Composable
fun PantallaEstats(viewModel: ViewModelEstats = viewModel())
{
    val estat = viewModel.estat.collectAsState()
    var mostraDialegAfegeixEstat by remember { mutableStateOf(false) }
    var mostraDialegActualitzaEstat by remember { mutableStateOf(false) }
    var estatEditat = Estat()

    val afegeixEstat = { estat: Estat -> viewModel.afegeixEstat(estat) }
    val eliminaEstat = { id: String -> viewModel.eliminaEstat(id) }
    val actualizaEstat = {estat:Estat -> viewModel.actualitzaEstat(estat) }
    val editaEstat = { estat: Estat ->
        estatEditat = estat
        mostraDialegActualitzaEstat = true
    }


    val onDialogDismissed = {
        mostraDialegAfegeixEstat = false
        mostraDialegActualitzaEstat = false
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                mostraDialegAfegeixEstat = true
            })
        {Icon(Icons.Filled.Add, "Floating action button.") }
        if (mostraDialegAfegeixEstat){
            DialegAfegeixEstat(afegeixEstat, onDialogDismissed)
        }
            if (mostraDialegActualitzaEstat){
                DialegActualitzaEstat (estatEditat, actualizaEstat, onDialogDismissed)
            }
        }
    )
    {padding->

        LazyColumn (modifier = Modifier.padding(padding))
        {
            items(estat.value.estats)
            {
                ElementEstat(it, eliminaEstat, editaEstat)
                Button(onClick = { viewModel.generaEstatsFake() }) {
                    Text("Genera estats")
                }
                Button(onClick = { viewModel.generaDadesFake() }) {
                    Text("Dades Fake")
                }
            }
        }

    }
}

@Preview
@Composable
fun ElementEstat(
    estat: Estat = Estat(
        id = "id",
        nom = "Nom",
        colorFons = "#FF444444",
        colorText = "#FFBBBBBB"
    ),
    eliminaEstat: (String) -> Unit={},
    editaEstat: (Estat) -> Unit={}
)
{
    Row(
        modifier=Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color= Color(estat.colorFons.toColorInt()))
    ){

        Text(
            text = estat.nom,
            modifier = Modifier.padding(8.dp)
                .weight(1F),
            style = MaterialTheme.typography.displayMedium,
            color = Color(estat.colorText.toColorInt())
        )
        IconButton(
            onClick = {eliminaEstat(estat.id)},
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Filled.Delete, "Elimina", tint = MaterialTheme.colorScheme.onError)
        }
        IconButton(
            onClick = { editaEstat(estat)},
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Filled.Edit, "Modifica", tint = MaterialTheme.colorScheme.onSecondary)
        }
    }
}


@Composable
fun DialegAfegeixEstat(afegeixEstat: (Estat) -> Unit, onDialogDismissed: () -> Unit) {
    var nom by remember { mutableStateOf("") }
    var colorText by remember { mutableStateOf("#FF000000") }
    var colorFons by remember { mutableStateOf("#FFFFFFFF") }
    val controladoPaletaDeColors = rememberColorPickerController()
    var seleccionaFons by remember { mutableStateOf(false) }


    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Afegeix un estat nou") },
        confirmButton = {
            if (nom.isNotEmpty()) {
                Button(
                    onClick = {
                            val estatNou = Estat(
                                nom = nom,
                                colorText = colorText,
                                colorFons = colorFons
                            )
                            afegeixEstat(estatNou)
                            nom = ""
                            colorText = "#FF000000"
                            colorFons = "#FFFFFFFF"
                            onDialogDismissed()
                        }
                ) {
                    Text(text = "Afegeix")
                }
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDialogDismissed()
                }
            ) {
                Text(text = "Cancel·la")
            }
        },
        text = {
            Column {
                Text(
                    text = nom,
                    modifier = Modifier.padding(8.dp)
                        .fillMaxWidth()
                        .background(Color(colorFons.toColorInt())),
                    style = MaterialTheme.typography.displayMedium,
                    color = Color(colorText.toColorInt())
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth().height(2.dp))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = nom,
                    onValueChange = { nom = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    label = { Text(text = "Nom de l'estat") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row()
                {
                    TextField(
                        modifier = Modifier
                            .weight(3F),
                        value = colorFons,
                        onValueChange = { colorFons = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii),
                        maxLines = 1,
                        label = { Text(text = "Color del fons") }

                    )
                    IconButton(
                        onClick = { seleccionaFons = true },
                        modifier = Modifier
                            .weight(1F)
                            .padding(8.dp)
                            .align(Alignment.CenterVertically)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .alpha(if (seleccionaFons) 1F else 0.7F)

                    ) {
                        Icon(
                            if (seleccionaFons)
                                Icons.Filled.Edit
                            else
                                Icons.Filled.DoDisturb,
                            "Modifica", tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

                Row()
                {
                    TextField(
                        modifier = Modifier
                            .weight(3F),
                        value = colorText,
                        onValueChange = { colorText = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii),
                        maxLines = 1,
                        label = { Text(text = "Color del text") }
                    )
                    IconButton(
                        onClick = { seleccionaFons = false },
                        modifier = Modifier
                            .weight(1F)
                            .padding(8.dp)
                            .align(Alignment.CenterVertically)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .alpha(if (!seleccionaFons) 1F else 0.7F)

                    ) {
                        Icon(
                            if (seleccionaFons == false)
                                Icons.Filled.Edit
                            else
                                Icons.Filled.DoDisturb,
                            "Modifica", tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(start = 8.dp),
                    controller = controladoPaletaDeColors,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        if (seleccionaFons) {
                            colorFons = "#${colorEnvelope.hexCode}"
                        } else {
                            colorText = "#${colorEnvelope.hexCode}"
                        }
                    }
                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                        .height(25.dp),
                    controller = controladoPaletaDeColors,
                )
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                        .height(25.dp),
                    controller = controladoPaletaDeColors,
                )
            }
        }
    )
}

@Composable
fun DialegActualitzaEstat(estat: Estat, actualitzaEstat: (Estat) -> Unit, onDialogDismissed: () -> Unit) {
    var nom by remember { mutableStateOf(estat.nom) }
    var colorText by remember { mutableStateOf(estat.colorText) }
    var colorFons by remember { mutableStateOf(estat.colorFons) }
    val controladoPaletaDeColors = rememberColorPickerController()
    var seleccionaFons by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDialogDismissed,
        title = { Text(text = "Modifica l'estat") },
        confirmButton = {
            if (nom.isNotEmpty()) {
                Button(
                    onClick = {
                        val estatNou = Estat(
                            id = estat.id,
                            nom = nom,
                            colorText = colorText,
                            colorFons = colorFons
                        )
                        actualitzaEstat(estatNou)
                        nom = ""
                        colorText = "#FF000000"
                        colorFons = "#FFFFFFFF"
                        onDialogDismissed()
                    }
                ) {
                    Text(text = "Modifica")
                }
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDialogDismissed()
                }
            ) {
                Text(text = "Cancel·la")
            }
        },
        text = {
            Column {
                Text(
                    text = nom,
                    modifier = Modifier.padding(8.dp)
                        .fillMaxWidth()
                        .background(Color(colorFons.toColorInt())),
                    style = MaterialTheme.typography.displayMedium,
                    color = Color(colorText.toColorInt())
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth().height(2.dp))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = nom,
                    onValueChange = { nom = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    label = { Text(text = "Nom de l'estat") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row()
                {
                    TextField(
                        modifier = Modifier
                            .weight(3F),
                        value = colorFons,
                        onValueChange = { colorFons = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii),
                        maxLines = 1,
                        label = { Text(text = "Color del fons") }

                    )
                    IconButton(
                        onClick = { seleccionaFons = true },
                        modifier = Modifier
                            .weight(1F)
                            .padding(8.dp)
                            .align(Alignment.CenterVertically)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .alpha(if(seleccionaFons) 1F else 0.7F)

                    ){
                        Icon(
                            if (seleccionaFons)
                                Icons.Filled.Edit
                            else
                                Icons.Filled.DoDisturb,
                            "Modifica", tint = MaterialTheme.colorScheme.onSecondary)
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

                Row()
                {
                    TextField(
                        modifier = Modifier
                            .weight(3F),
                        value = colorText,
                        onValueChange = { colorText = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii),
                        maxLines = 1,
                        label = { Text(text = "Color del text") }
                    )
                    IconButton(
                        onClick = { seleccionaFons = false },
                        modifier = Modifier
                            .weight(1F)
                            .padding(8.dp)
                            .align(Alignment.CenterVertically)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .alpha(if(!seleccionaFons) 1F else 0.7F)

                    ){
                        Icon(
                            if (seleccionaFons==false)
                                Icons.Filled.Edit
                            else
                                Icons.Filled.DoDisturb,
                            "Modifica", tint = MaterialTheme.colorScheme.onSecondary)
                    }
                }
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(start = 8.dp),
                    controller = controladoPaletaDeColors,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        if (seleccionaFons){
                        colorFons = "#${colorEnvelope.hexCode}"
                            }
                        else
                        {
                            colorText = "#${colorEnvelope.hexCode}"
                        }
                    }
                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                        .height(25.dp),
                    controller = controladoPaletaDeColors,
                )
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                        .height(25.dp),
                    controller = controladoPaletaDeColors,
                )
            }
        }
    )
}


