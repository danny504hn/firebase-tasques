package cat.institutmontilivi.tasquesfirebase25.ui.pantalles
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.institutmontilivi.tasquesfirebase25.dades.local.TipusMultimedia
import cat.institutmontilivi.tasquesfirebase25.dades.local.creaUnFitxerMultimedia
import cat.institutmontilivi.tasquesfirebase25.model.app.Estat
import cat.institutmontilivi.tasquesfirebase25.model.app.Tasca
import cat.institutmontilivi.tasquesfirebase25.ui.viewmodels.ViewModelTasques
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import java.time.LocalDate
import java.util.Objects

@Composable
fun PantallaTasques(viewModel: ViewModelTasques = viewModel(), navegaAFoto: (String) -> Unit) {
    val estat = viewModel.tasques.collectAsState()
    var mostraDialegAfegeixTasca by remember { mutableStateOf(false) }
    var mostraDialegActualitzaTasca by remember { mutableStateOf(false) }
    var tascaEditada = Tasca()

    val afegeixTasca = { tasca: Tasca -> viewModel.afegeixTasca(tasca) }
    val eliminaTasca = { id: String -> viewModel.eliminaTasca(id) }
    val actualizaTasca = { tasca: Tasca -> viewModel.actualitzaTasca(tasca) }


    val editaTasca = { tasca: Tasca ->
        tascaEditada = tasca
        mostraDialegActualitzaTasca = true
    }

    val onDialogDismissed = {
        mostraDialegAfegeixTasca = false
        mostraDialegActualitzaTasca = false
    }
    val context = LocalContext.current

    //region launchers
    //Cal crear un fitxerbuit, que el launcher farà servir per emmagatzemar el fitxer multimèdia
    val fitxerImatge = context.creaUnFitxerMultimedia(TipusMultimedia.IMATGE)
    val fitxerVideo = context.creaUnFitxerMultimedia(TipusMultimedia.VIDEO)
    val fitxerAudio = context.creaUnFitxerMultimedia(TipusMultimedia.AUDIO)

    //Cal exposar el fitxer a traves del provider
    // Cal configurar el provider al manifest
    // el fixer dels recursos file_paths està marcat com a cache, perquè serà temporal
    val uriImatge = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        "cat.institutmontilivi.tasquesfirebase25.proveidor", fitxerImatge
    )

    val uriVideo = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        "cat.institutmontilivi.tasquesfirebase25.proveidor", fitxerVideo
    )

    var uriDeLaImatgeCapturada by remember { mutableStateOf<Uri>(Uri.EMPTY) }
    var uriDeLVideoCapturat by remember { mutableStateOf<Uri>(Uri.EMPTY) }

    val cameraImatgeLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {success->
            if (success) {
                uriDeLaImatgeCapturada = uriImatge
                uriDeLaImatgeCapturada?.let { uri ->
                    viewModel.afegeixFoto(uri)

                }
            }
        }
    val cameraVideoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) {success->
            if (success) {
                uriDeLVideoCapturat = uriVideo
                uriDeLVideoCapturat?.let { uri ->
                    viewModel.afegeixVideo(uri)
                }
            }
        }
            //enregions

            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        mostraDialegAfegeixTasca = true
                    })
                    { Icon(Icons.Filled.Add, "Floating action button.") }
                    if (mostraDialegAfegeixTasca) {
                        DialegAfegeixTasca(
                            estat.value.usuariActual,
                            estat.value.estats,
                            afegeixTasca,
                            onDialogDismissed
                        )
                    }
                    if (mostraDialegActualitzaTasca) {
                        DialegActualitzaTasca(
                            estat.value.usuariActual,
                            estat.value.estats,
                            tascaEditada,
                            actualizaTasca,
                            onDialogDismissed
                        )
                    }
                }
            )
            { padding ->
                LazyColumn(modifier = Modifier.padding(padding))
                {
                    items(estat.value.tasques)
                    {
                        ElementTasca(
                            it,
                            eliminaTasca,
                            editaTasca,
                            estat.value.estats,
                            cameraImatgeLauncher,
                            uriImatge,
                            cameraVideoLauncher,
                            uriVideo,
                            viewModel,
                            navegaAFoto
                        )
                    }
                }
            }
        }


    //region Element de tasca
//@Preview
    @Composable
    fun ElementTasca(
        tasca: Tasca = Tasca(),
        eliminaTasca: (String) -> Unit = {},
        editaTasca: (Tasca) -> Unit = {},
        estats: Map<String, Estat> = mapOf(),
        cameraImatgeLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
        uriImatge: Uri,
        cameraVideoLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
        uriVideo: Uri,
        viewModel: ViewModelTasques,
        navegaAFoto: (String) -> Unit,

        ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    color = Color(
                        (estats[tasca.estat]?.colorFons ?: "#FF000000").toColorInt()
                    )
                )
        ) {

            Text(
                text = tasca.titol,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.headlineLarge,
                color = Color((estats[tasca.estat]?.colorText ?: "#FFFFFFFF").toColorInt()),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = tasca.descripcio,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color((estats[tasca.estat]?.colorText ?: "#FFFFFFFF").toColorInt()),
                textAlign = TextAlign.Center
            )

            //region Botons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                IconButton(
                    onClick = { eliminaTasca(tasca.id) },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Filled.Delete, "Elimina", tint = MaterialTheme.colorScheme.onError)
                }
                IconButton(
                    onClick = { editaTasca(tasca) },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        "Modifica",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        Icons.Filled.Share,
                        "Comparteix",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.tascaActual = tasca
                        cameraImatgeLauncher.launch(uriImatge)
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Filled.Photo, "Foto", tint = MaterialTheme.colorScheme.onSecondary)
                }
                IconButton(
                    onClick = {
                        viewModel.tascaActual = tasca
                        cameraVideoLauncher.launch(uriVideo)
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        Icons.Filled.Videocam,
                        "Vídeo",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
            //endregion
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .horizontalScroll(scrollState)
            ){
                tasca.uriFotos.forEach{
                    ImatgeDeCoil(
                        urlDeLaImatge = it,
                        contentDescription = "",
                        modifier = Modifier
                            .height(150.dp)
                            .width(200.dp)
                            .padding(2.dp)
                            .background(Color.Cyan)
                            .clickable { navegaAFoto(it) },
                        contentScale = ContentScale.Fit)
                }
            }
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp)
//                    .background(MaterialTheme.colorScheme.primaryContainer)
//                    .horizontalScroll(scrollState)
//            ){
//                tasca.uriVideos.forEach{
//                    ImatgeDeCoil(urlDeLaImatge = it, contentDescription = "", modifier = Modifier.height(150.dp).width(200.dp).padding(2.dp).background(Color.Cyan), contentScale = ContentScale.Fit)
//                }
//            }
        }
    }
//endregion

    //region Dialegs Afegeix Tasca i actualitza tasca
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DialegAfegeixTasca(
        usuariActual: String,
        estats: Map<String, Estat>,
        afegeixTasca: (Tasca) -> Unit,
        onDialogDismissed: () -> Unit
    ) {
        var titol by remember { mutableStateOf("") }
        var descripcio by remember { mutableStateOf("") }
        var dataLimit by remember { mutableStateOf(LocalDate.now().toEpochDay()) }
        val estatDatePicker = rememberDatePickerState()
        var desplegat by remember { mutableStateOf(false) }
        var estat by remember { mutableStateOf(estats.keys.first()) }


        AlertDialog(
            onDismissRequest = onDialogDismissed,
            title = { Text(text = "Afegeix una tasca nova") },
            confirmButton = {
                if (titol.isNotEmpty()) {
                    Button(
                        onClick = {
                            val tascaNova = Tasca(
                                titol = titol,
                                descripcio = descripcio,
                                dataLimit = dataLimit,
                                estat = estat,
                                usuaris = listOf(usuariActual)
                            )
                            afegeixTasca(tascaNova)
                            titol = ""
                            descripcio = ""
                            dataLimit =
                                estatDatePicker.selectedDateMillis ?: LocalDate.now().toEpochDay()
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
                    TextField(
                        value = titol,
                        onValueChange = { titol = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        maxLines = 1,
                        label = { Text(text = "Títol de la tasca") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = descripcio,
                        onValueChange = { descripcio = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        maxLines = 8,
                        label = { Text(text = "Descripció de la tasca") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        Row {
                            Text(
                                text = estats[estat]!!.nom,
                                color = Color(estats[estat]!!.colorText.toColorInt()),
                                modifier = Modifier
                                    .background(Color(estats[estat]!!.colorFons.toColorInt()))
                                    .padding(8.dp),
                                style = MaterialTheme.typography.headlineLarge
                            )

                            IconButton(onClick = { desplegat = !desplegat }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More"
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = desplegat,
                            onDismissRequest = { desplegat = false }
                        ) {
                            estats.forEach {
                                DropdownMenuItem(
                                    text = { Text(it.value.nom) },
                                    onClick = { estat = it.key; desplegat = false }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePicker(
                        state = estatDatePicker,
                        title = { Text(text = "Data límit de la tasca") },
                        headline = {
                            Text("Data")
                        },
                        showModeToggle = true
                    )

                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DialegActualitzaTasca(
        usuariActual: String,
        estats: Map<String, Estat>,
        tasca: Tasca,
        actualizaTasca: (Tasca) -> Unit,
        onDialogDismissed: () -> Unit
    ) {
        var titol by remember { mutableStateOf(tasca.titol) }
        var descripcio by remember { mutableStateOf(tasca.descripcio) }
        var dataLimit by remember { mutableStateOf(tasca.dataLimit) }
        val estatDatePicker = rememberDatePickerState()
        var desplegat by remember { mutableStateOf(false) }
        var estat by remember { mutableStateOf(tasca.estat) }


        AlertDialog(
            onDismissRequest = onDialogDismissed,
            title = { Text(text = "Actualitza la tasca") },
            confirmButton = {
                if (titol.isNotEmpty()) {
                    Button(
                        onClick = {
                            val tascaNova = Tasca(
                                id = tasca.id,
                                titol = titol,
                                descripcio = descripcio,
                                dataLimit = dataLimit,
                                estat = estat,
                                usuaris = listOf(usuariActual)
                            )
                            actualizaTasca(tascaNova)
                            titol = ""
                            descripcio = ""
                            dataLimit =
                                estatDatePicker.selectedDateMillis ?: LocalDate.now().toEpochDay()
                            onDialogDismissed()
                        }
                    ) {
                        Text(text = "Actualiza")
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
                    TextField(
                        value = titol,
                        onValueChange = { titol = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        maxLines = 1,
                        label = { Text(text = "Títol de la tasca") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = descripcio,
                        onValueChange = { descripcio = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        maxLines = 8,
                        label = { Text(text = "Descripció de la tasca") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        Row {
                            Text(
                                text = estats[estat]!!.nom,
                                color = Color(estats[estat]!!.colorText.toColorInt()),
                                modifier = Modifier
                                    .background(Color(estats[estat]!!.colorFons.toColorInt()))
                                    .padding(8.dp),
                                style = MaterialTheme.typography.headlineLarge
                            )

                            IconButton(onClick = { desplegat = !desplegat }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More"
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = desplegat,
                            onDismissRequest = { desplegat = false }
                        ) {
                            estats.forEach {
                                DropdownMenuItem(
                                    text = { Text(it.value.nom) },
                                    onClick = { estat = it.key; desplegat = false }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePicker(
                        state = estatDatePicker,
                        title = { Text(text = "Data límit de la tasca") },
                        headline = {
                            Text("Data")
                        },
                        showModeToggle = true
                    )
                }
            }
        )
    }
//endregion

@Composable
fun ImatgeDeCoil(
    urlDeLaImatge: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val painter = rememberAsyncImagePainter(
        ImageRequest
            .Builder(LocalContext.current)
            .data(data = urlDeLaImatge)
            .apply(block = fun ImageRequest.Builder.() {
                crossfade(true)
                transformations(RoundedCornersTransformation(topLeft = 20f, topRight = 20f, bottomLeft = 20f, bottomRight = 20f))
            })
            .build()
    )

    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.padding(6.dp),
        contentScale = contentScale,
    )
}


@Composable
fun VideoDeCoil(
    uriDelVideo: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val painter = rememberAsyncImagePainter(
        ImageRequest
            .Builder(LocalContext.current)
            .data(data = uriDelVideo)
            //.videoFrameMillis(1000)
            //.decoderFactory{}
            .apply(block = fun ImageRequest.Builder.() {
                crossfade(true)
                transformations(RoundedCornersTransformation(topLeft = 20f, topRight = 20f, bottomLeft = 20f, bottomRight = 20f))
            })
            .build()
    )

    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.padding(6.dp),
        contentScale = contentScale,
    )
}