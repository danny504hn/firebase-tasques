package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase

import cat.institutmontilivi.tasquesfirebase25.dades.EstatsRepositori
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.model.app.Estat
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EstatsFirebaseRemoteDataSource( manegadorFirestore: ManegadorFirestore) : EstatsRepositori {
    val db = manegadorFirestore

    override suspend fun obtenEstats(): Flow<Resposta<List<Estat>>>  =
        TODO("Not yet implemented")

    override suspend fun afegeixEstat(estat: Estat): Resposta<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun eliminaEstat(id: String): Resposta<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun actualitzaEstat(estat: Estat): Resposta<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun existeixEstat(nom: String): Resposta<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun obtenEstat(id: String): Resposta<Estat> {
        TODO("Not yet implemented")
    }
}

