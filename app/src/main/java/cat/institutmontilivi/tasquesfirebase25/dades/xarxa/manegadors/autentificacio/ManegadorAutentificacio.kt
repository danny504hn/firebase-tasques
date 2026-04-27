package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.autentificacio

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import cat.institutmontilivi.tasquesfirebase25.R
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException



class ManegadorAutentificacio (private val context: Context){
    //En Android amb Firebase, el plugin com.google.gms.google-services et crea el recurs
    // R.string.default_web_client_id automàticament.
    private val idClientWeb by lazy { context.getString(R.string.default_web_client_id) }
    private val tag = "MANEGADOR_AUTENTIFICACIO"
    private val autentificacio: FirebaseAuth by lazy {
        Firebase.auth
    }


    //private val manegadorDeCredencials = CredentialManager.create(context)


    suspend fun iniciaSessioAnonima(): Resposta<FirebaseUser> {
        return try {
            val result = autentificacio.signInAnonymously().await()
            Resposta.Exit(result.user ?: throw Exception("Error a l'iniciar la sessió"))
        } catch(e: Exception) {
            Resposta.Fracas(e.message ?: "Error a l'iniciar la sessió")
        }
    }



    suspend fun creaUsuariAmbCorreuIMotDePas(correu: String, motDePas: String): Resposta<FirebaseUser?> {
        TODO("Not yet implemented")
    }

    suspend fun iniciaSessioAmbCorreuIMotDePas(correu: String, motDePas: String): Resposta<FirebaseUser?> {
        return try {
            val authResult = autentificacio.signInWithEmailAndPassword(correu, motDePas).await()
            Resposta.Exit(authResult.user)
        } catch(e: Exception) {
            Resposta.Fracas(e.message ?: "Error a l'iniciar la sessió")
        }
    }

    suspend fun restableixElMotDePas(correu: String): Resposta<Unit> {
        TODO("Not yet implemented")
    }

    suspend fun tancaSessio() {

        //manegadorDeCredencials.clearCredentialState(ClearCredentialStateRequest())
        autentificacio.signOut()
    }

    fun obtenUsuariActual(): FirebaseUser?{
        return autentificacio.currentUser
    }

    fun hiHaUsuariIniciat() =obtenUsuariActual() != null


    private suspend fun creaPeticioDeCredencials(): GetCredentialResponse
    {
        TODO("Not yet implemented")
    }

    private suspend fun manegaIniciDeSessio(resultat: GetCredentialResponse): Boolean {
        TODO("Not yet implemented")
    }

    suspend fun iniciDeSessioAmbGoogle():Boolean {
        TODO("Not yet implemented")
    }
}