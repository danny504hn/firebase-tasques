package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase
import cat.institutmontilivi.tasquesfirebase25.dades.BBDDFactory
import cat.institutmontilivi.tasquesfirebase25.dades.TasquesRepositori
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.usuariActual
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import cat.institutmontilivi.tasquesfirebase25.model.app.Tasca
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class TasquesFirebaseRemoteDataSource(manegadorFirestore: ManegadorFirestore): TasquesRepositori {
    val db = manegadorFirestore

    override suspend fun obtenTasques(idUsuari: String): Flow<Resposta<List<Tasca>>> = callbackFlow{
        TODO("Not yet implemented")
    }

    override suspend fun afegeixTasca(tasca: Tasca): Resposta<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun eliminaTasca(idTasca: String): Resposta<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun actualitzaTasca(tasca: Tasca): Resposta<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun existeixTasca(titol: String): Resposta<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun obtenTasca(idTasca: String): Resposta<Tasca> {
        TODO("Not yet implemented")
    }

    override suspend fun convidaUsuari(idTasca: String, idUsuari: String): Resposta<Tasca> {
        TODO("Not yet implemented")
    }
}