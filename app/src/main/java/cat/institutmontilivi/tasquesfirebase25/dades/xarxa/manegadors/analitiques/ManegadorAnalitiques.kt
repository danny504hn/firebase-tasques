package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.analitiques

import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.google.firebase.analytics.FirebaseAnalytics

class ManegadorAnalitiques(context: Context)  {
    private val firebaseAnalytics: FirebaseAnalytics by lazy { FirebaseAnalytics.getInstance(context) }
    private fun registraEsdeveniment(nomEsdeveniment: String, params: Bundle) {
        firebaseAnalytics.logEvent(nomEsdeveniment, params)
    }

    fun registreClicABoto(nomBoto: String) {
        val params = Bundle().apply {
            putString("nom_del_botó", nomBoto)
        }
        registraEsdeveniment("botó_clicat", params)
    }


    fun registraError(error: String) {
        val params = Bundle().apply {
            putString("error", error)
        }
        registraEsdeveniment("error", params)
    }
    @Composable
    fun  registraVisitaAPantalla(nomPantalla: String) {
        DisposableEffect(Unit) {
            onDispose {
                val params = Bundle().apply {
                    putString(FirebaseAnalytics.Param.SCREEN_NAME, nomPantalla)
                    putString(FirebaseAnalytics.Param.SCREEN_CLASS, nomPantalla)
                }
                registraEsdeveniment(FirebaseAnalytics.Event.SCREEN_VIEW, params)
            }
        }
    }
}