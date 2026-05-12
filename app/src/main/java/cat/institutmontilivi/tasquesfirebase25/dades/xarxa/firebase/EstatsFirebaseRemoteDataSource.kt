package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase

import cat.institutmontilivi.tasquesfirebase25.dades.EstatsRepositori
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.usuariActual.id
import cat.institutmontilivi.tasquesfirebase25.model.app.Estat
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class EstatsFirebaseRemoteDataSource( manegadorFirestore: ManegadorFirestore) : EstatsRepositori {
    val db = manegadorFirestore

//    override suspend fun obtenEstats(): Flow<Resposta<List<Estat>>>  =
//        callbackFlow {
//            var llista : List<Estat>? = null
//            var subscripcio : ListenerRegistration? = null
//            try{
//                val refEstat = db.firestoreDb.collection(db.ESTATS)
//                refEstat.addSnapshotListener { snapshots, exception ->
//                    val llista = snapshots?.toObjects<Estat>()
//                    if(llista ==null){
//                        trySend(Resposta.Fracas("No s'ha pogut enviar la llista"))
//                    }else
//                    trySend(Resposta.Exit(llista))
//                }
//            }catch (e : Exception){
//                trySend(Resposta.Fracas("No s'ha pogut enviar la llista"))
//            }finally {
//                awaitClose {
//                    subscripcio?.remove()
//                }
//
//            }
//        }

    override suspend fun obtenEstats(): Flow<Resposta<List<Estat>>> =
        db.firestoreDb.collection(db.ESTATS)
            .snapshots()
            .map{ querySnapshot ->
                val estats = querySnapshot.documents
                    .mapNotNull{ documentSnapshot ->
                        documentSnapshot.toObject(Estat::class.java)
                    }
                Resposta.Exit(estats) as Resposta<List<Estat>>

            }
            .catch {error ->
                emit(Resposta.Fracas(error.message ?: "Error en obtenir els estats"))
            }


    override suspend fun afegeixEstat(estat: Estat): Resposta<Boolean> {
        var existeix = false
        try {

            val resposta = existeixEstat(estat.nom)
            if(resposta is Resposta.Exit) {
                existeix = resposta.dades
            }else if (resposta is Resposta.Fracas){
                throw Exception(resposta.missatgeError)
            }
            if(!existeix){
                val refEstats = db.firestoreDb.collection(db.ESTATS)
                val refEstatNou = refEstats.document()
                val estatAmbId = estat.copy(id= refEstatNou.id)
                refEstatNou.set(estatAmbId).await()
            }


        }catch (e : Exception){
            return Resposta.Fracas(missatgeError = e.message ?: "error afegin un estat nou")
        }
        return Resposta.Exit(!existeix)
    }

    override suspend fun eliminaEstat(id: String): Resposta<Boolean> {
        var eliminat = false
        try {
            var refEstats = db.firestoreDb.collection(db.ESTATS)
            var refEstat = refEstats.document(id)
            var doc = refEstat.delete().await()
            eliminat = true
        }catch (e : Exception){
            return (Resposta.Fracas("Ha fallat"))
        }
       return  Resposta.Exit(true)
    }

    override suspend fun actualitzaEstat(estat: Estat): Resposta<Boolean> {
        var actualitzat = false
        try {
            var refEstats = db.firestoreDb.collection(db.ESTATS)
            var refEstat = refEstats.document(estat.id)
            var doc = refEstat.set(estat).await()
            actualitzat = true
        }catch (e : Exception){
            return (Resposta.Fracas("Ha fallat"))
        }
       return Resposta.Exit(actualitzat)
    }

    override suspend fun existeixEstat(nom: String): Resposta<Boolean> {
        var noExisteix = false
        try {
            val refEstats = db.firestoreDb.collection(db.ESTATS) // ruta hacia la collection
            val consulta = refEstats.whereEqualTo("nom",nom)
            noExisteix = consulta
                 .get()
                 .await()
                 .documents.isEmpty()

        }catch (e : Exception){
            return (Resposta.Fracas("Ha fallat"))
        }
        return Resposta.Exit(!noExisteix)
    }

    override suspend fun obtenEstat(id: String): Resposta<Estat> {
        var estat : Estat = Estat()
        try{
            val refEstat = db.firestoreDb.collection(db.ESTATS)
            val consulta = refEstat.document(id)
            val doc = consulta.get().await()
            estat = doc.toObject<Estat>() ?: Estat()
        }catch (e : Exception){
            return (Resposta.Fracas("Ha fallat"))
        }
        return Resposta.Exit(estat)

    }
}

