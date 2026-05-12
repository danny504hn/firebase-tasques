package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase

import cat.institutmontilivi.tasquesfirebase25.dades.TasquesRepositori
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import cat.institutmontilivi.tasquesfirebase25.model.app.Tasca
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TasquesFirebaseRemoteDataSource(manegadorFirestore: ManegadorFirestore) : TasquesRepositori {

    val db = manegadorFirestore

    override suspend fun obtenTasques(idUsuari: String): Flow<Resposta<List<Tasca>>> = callbackFlow {
        var subscripcio: ListenerRegistration? = null
        subscripcio = db.firestoreDb.collection(db.TASQUES)
            .whereArrayContains("usuaris", idUsuari)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resposta.Fracas(error.message ?: "Error Firestore"))
                    return@addSnapshotListener
                }
                val llista = snapshot?.toObjects(Tasca::class.java) ?: emptyList()
                trySend(Resposta.Exit(llista))
            }
        awaitClose { subscripcio.remove() }
    }

    override suspend fun obtenTasca(idTasca: String): Resposta<Tasca> {
        return try {
            val document = db.firestoreDb.collection(db.TASQUES).document(idTasca).get().await()
            val tasca = document.toObject(Tasca::class.java) ?: Tasca()
            Resposta.Exit(tasca)
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error al obtenir la tasca")
        }
    }

    override suspend fun afegeixTasca(tasca: Tasca): Resposta<Boolean> {
        return try {
            val existeix = existeixTasca(tasca.titol)
            if (existeix is Resposta.Fracas) throw Exception(existeix.missatgeError)
            if ((existeix as Resposta.Exit).dades) return Resposta.Exit(false)

            val ref = db.firestoreDb.collection(db.TASQUES).document()
            val tascaAmbId = tasca.copy(id = ref.id)
            ref.set(tascaAmbId).await()
            Resposta.Exit(true)
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error al crear la tasca")
        }
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────────
    override suspend fun actualitzaTasca(tasca: Tasca): Resposta<Boolean> {
        return try {
            db.firestoreDb.collection(db.TASQUES).document(tasca.id).set(tasca).await()
            Resposta.Exit(true)
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error al actualitzar la tasca")
        }
    }

    // ── DELETE ─────────────────────────────────────────────────────────────────
    override suspend fun eliminaTasca(idTasca: String): Resposta<Boolean> {
        return try {
            db.firestoreDb.collection(db.TASQUES).document(idTasca).delete().await()
            Resposta.Exit(true)
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error al eliminar la tasca")
        }
    }

    // ── EXISTEIX ───────────────────────────────────────────────────────────────
    override suspend fun existeixTasca(titol: String): Resposta<Boolean> {
        return try {
            val query = db.firestoreDb.collection(db.TASQUES)
                .whereEqualTo("titol", titol)
                .get()
                .await()
            Resposta.Exit(!query.isEmpty)
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error al consultar titol")
        }
    }

    // ── CONVIDA USUARI ─────────────────────────────────────────────────────────
    // Afegeix l'idUsuari a la llista "usuaris" de la tasca i retorna la tasca actualitzada
    override suspend fun convidaUsuari(idTasca: String, idUsuari: String): Resposta<Tasca> {
        return try {
            val refTasca = db.firestoreDb.collection(db.TASQUES).document(idTasca)
            refTasca.update("usuaris", FieldValue.arrayUnion(idUsuari)).await()

            val document = refTasca.get().await()
            val tasca = document.toObject(Tasca::class.java)
                ?: return Resposta.Fracas("No s'ha trobat la tasca")
            Resposta.Exit(tasca)
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error al convidar l'usuari a la tasca")
        }
    }
}