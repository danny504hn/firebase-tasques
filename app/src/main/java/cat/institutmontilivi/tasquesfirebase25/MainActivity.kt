package cat.institutmontilivi.tasquesfirebase25

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.analitiques.ManegadorAnalitiques
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.autentificacio.ManegadorAutentificacio
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.ui.Aplicacio
import cat.institutmontilivi.tasquesfirebase25.ui.theme.Tasquesfirebase25Theme
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var manegadorFirestore = ManegadorFirestore()
        var manegadorAutentificacio = ManegadorAutentificacio(this)
        val crashlytics = FirebaseCrashlytics.getInstance()
        val manegadorAnalitiques = ManegadorAnalitiques(this)
        setContent {
            Tasquesfirebase25Theme {
                Aplicacio(manegadorFirestore= manegadorFirestore,
                    manegadorAutentificacio = manegadorAutentificacio,
                    crashlytics = crashlytics,
                    manegadorAnalitiques = manegadorAnalitiques)
            }
        }
    }
}

