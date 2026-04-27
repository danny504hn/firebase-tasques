package cat.institutmontilivi.tasquesfirebase25.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cat.institutmontilivi.tasquesfirebase25.R
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.analitiques.ManegadorAnalitiques
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.autentificacio.ManegadorAutentificacio
import cat.institutmontilivi.tasquesfirebase25.dades.BBDDFactory
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.usuariActual
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import cat.institutmontilivi.tasquesfirebase25.model.app.Usuari
import cat.institutmontilivi.tasquesfirebase25.navegacio.DestinacioEstat
import cat.institutmontilivi.tasquesfirebase25.navegacio.DestinacioEstats
import cat.institutmontilivi.tasquesfirebase25.navegacio.DestinacioInstruccions
import cat.institutmontilivi.tasquesfirebase25.navegacio.DestinacioPerfil
import cat.institutmontilivi.tasquesfirebase25.navegacio.DestinacioPortada
import cat.institutmontilivi.tasquesfirebase25.navegacio.DestinacioPreferencies
import cat.institutmontilivi.tasquesfirebase25.navegacio.DestinacioQuantA
import cat.institutmontilivi.tasquesfirebase25.navegacio.DestinacioRegistre
import cat.institutmontilivi.tasquesfirebase25.navegacio.DestinacioTasca
import cat.institutmontilivi.tasquesfirebase25.navegacio.DestinacioTasques
import cat.institutmontilivi.tasquesfirebase25.navegacio.GrafDeNavegacio
import cat.institutmontilivi.tasquesfirebase25.navegacio.opcionsDrawer
import cat.institutmontilivi.tasquesfirebase25.ui.theme.Tasquesfirebase25Theme
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PantallaDeLAplicacio (content: @Composable ()->Unit)
{
    Tasquesfirebase25Theme  {
        content()
    }
}

//region Aplicacio

@Composable
fun Aplicacio (
    content: @Composable () -> Unit = { Text("") },
    manegadorFirestore: ManegadorFirestore,
    manegadorAutentificacio: ManegadorAutentificacio,
    crashlytics: FirebaseCrashlytics,
    manegadorAnalitiques: ManegadorAnalitiques
)
{
    val context = LocalContext.current

    val controladorDeNavegacio = rememberNavController()
    val ambitCorrutina: CoroutineScope = rememberCoroutineScope()
    var estatDrawer = rememberDrawerState(initialValue = DrawerValue.Closed)
    val rutaActual by controladorDeNavegacio.currentBackStackEntryAsState()
    val destinacioActual = rutaActual?.destination


    CalaixDeNavegacio(controladorDeNavegacio, ambitCorrutina, estatDrawer, rutaActual, destinacioActual, manegadorAnalitiques, crashlytics, manegadorFirestore, manegadorAutentificacio)

}
//endregion

//region Drawer
@Composable
fun CalaixDeNavegacio(
    controladorDeNavegacio: NavHostController = rememberNavController(),
    ambitCorrutina: CoroutineScope = rememberCoroutineScope(),
    estatDrawer: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    rutaActual: NavBackStackEntry?,
    destinacioActual: NavDestination?,
    manegadorAnalitiques: ManegadorAnalitiques,
    crashlytics: FirebaseCrashlytics,
    manegadorFirestore: ManegadorFirestore,
    manegadorAutentificacio: ManegadorAutentificacio
) {
    val usuariAutenticat: FirebaseUser? = manegadorAutentificacio.obtenUsuariActual()
    val context = LocalContext.current

    LaunchedEffect (usuariAutenticat)
    {
        if (usuariAutenticat!= null) {
            ambitCorrutina.launch {
                val resposta = BBDDFactory.obtenRepositoriUsuaris(
                    context,
                    BBDDFactory.DatabaseType.FIREBASE
                ).obtenUsuari(usuariAutenticat.email ?: "")
                if (resposta is Resposta.Exit) {
                    usuariActual.id= resposta.dades.id
                    usuariActual.nom= resposta.dades.nom
                    usuariActual.correu= resposta.dades.correu
                    usuariActual.usuarisHabituals= resposta.dades.usuarisHabituals
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = estatDrawer,
        drawerContent = {
            ModalDrawerSheet (
                drawerShape = ShapeDefaults.Small, //fa referència a la mida del corner radius
                drawerContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                drawerContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                drawerTonalElevation = 5.dp,
                windowInsets = WindowInsets(left = 24.dp, right = 24.dp, top = 48.dp) // És el padding
            ){
                Icon(painter = painterResource(id = R.drawable.logomontilivi),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().aspectRatio(3F),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer (Modifier.height(48.dp))
                Divider(
                    modifier = Modifier.height(15.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer (Modifier.height(48.dp))
                opcionsDrawer.forEach {opcio->
                    NavigationDrawerItem  (
                        label = { Text(opcio.titol) },
                        selected = destinacioActual?.hierarchy?.any {it.hasRoute(opcio.ruta::class) } == true,
                        icon = {if (destinacioActual?.hierarchy?.any {it.hasRoute(opcio.ruta::class) } == true)
                            Icon(imageVector = opcio.iconaSeleccionada, contentDescription = opcio.titol)
                        else
                            Icon(imageVector = opcio.iconaNoSeleccionada, contentDescription = opcio.titol)
                        },
                        onClick = {
                            ambitCorrutina.launch {
                                estatDrawer.close()
                            }
                            var destinacio = opcio.ruta
                            if (destinacio is DestinacioTasques)
                                destinacio = DestinacioTasques(usuariActual.id)

                            controladorDeNavegacio.navigate(destinacio) {
                                popUpTo(controladorDeNavegacio.graph.findStartDestination().id){
                                    //guarda l'estat de la pantalla de la que marxem (funciona d'aquella manera,
                                    // No tots els valors es guarden))
                                    saveState = true
                                }
                                launchSingleTop = true
                                //Restaura l'estat de la pantalla i la deixa tal i com estava quan vam navegar a un altre lloc
                                restoreState = true
                            }  },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedBadgeColor = MaterialTheme.colorScheme.error,
                            unselectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedBadgeColor = MaterialTheme.colorScheme.error,
                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onTertiaryContainer),
                        badge = {
                            if (opcio.mostraInsignia)
                                Icon(imageVector = opcio.IconaInsignia, "Opció destacada")
                        },
                        shape = ShapeDefaults.Medium
                    )
                }
            }
        },
        gesturesEnabled = esPotObrirElDrawer(destinacioActual)
    ) {
        Bastida(
            rutaActual = rutaActual,
            destinacioActual = destinacioActual,
            controladorDeNavegacio = controladorDeNavegacio,
            ambitCorrutina = ambitCorrutina,
            estatDrawer = estatDrawer,
            manegadorAnalitiques = manegadorAnalitiques,
            crashlytics = crashlytics,
            manegadorFirestore,
            manegadorAutentificacio
        )
    }
}

private fun esPotObrirElDrawer(destinacioActual: NavDestination?) = listOf(
    DestinacioPortada::class,
    DestinacioInstruccions::class,
    DestinacioPreferencies::class,
    DestinacioQuantA::class,
    DestinacioPerfil::class,
    DestinacioTasques::class,
    DestinacioEstats::class,

).any { destinacioActual?.hasRoute(it) ?: true }

private fun esPotAnarEnrera(destinacioActual: NavDestination?) = listOf(
    DestinacioRegistre::class,
    DestinacioTasca::class,
    DestinacioEstat::class,
    ).any { destinacioActual?.hasRoute(it) ?: true }
//endregion


//region Bastida
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bastida(
    rutaActual: NavBackStackEntry?,
    destinacioActual: NavDestination?,
    controladorDeNavegacio: NavHostController,
    ambitCorrutina: CoroutineScope,
    estatDrawer: DrawerState,
    manegadorAnalitiques: ManegadorAnalitiques,
    crashlytics: FirebaseCrashlytics,
    manegadorFirestore: ManegadorFirestore,
    manegadorAutentificacio: ManegadorAutentificacio
)
{
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    if(esPotObrirElDrawer(destinacioActual)) {
                        IconButton(onClick = {
                            ambitCorrutina.launch {
                                if (estatDrawer.isClosed)
                                    estatDrawer.open()
                                else
                                    estatDrawer.close()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    else if (esPotAnarEnrera(destinacioActual)){
                        IconButton(onClick = {
                            controladorDeNavegacio.navigateUp()
                            manegadorAnalitiques.registreClicABoto("Fletxa enrera")
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    else {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Login,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                })
        }

    )
    {paddingValues ->
        GrafDeNavegacio(
            controladorDeNavegacio = controladorDeNavegacio,
            manegadorAnalitiques = manegadorAnalitiques,
            manegadorFirestore = manegadorFirestore,
            manegadorAutentificacio = manegadorAutentificacio,
            crashlytics = crashlytics,
            paddingValues = paddingValues
        )
    }
}
//endregion