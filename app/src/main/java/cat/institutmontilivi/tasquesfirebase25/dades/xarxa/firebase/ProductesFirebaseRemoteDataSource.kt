package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase

import cat.institutmontilivi.tasquesfirebase25.dades.ProductesRepositori
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.model.app.Producte
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ProductesFirebaseRemoteDataSource(manegadorFirestore: ManegadorFirestore) : ProductesRepositori {

    val db = manegadorFirestore

    override suspend fun obtenProductes(): Flow<Resposta<List<Producte>>> =
        db.firestoreDb.collection(db.PRODUCTES)
            .snapshots()
            .map { querySnapshot ->
                val productes = querySnapshot.documents
                    .mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject(Producte::class.java)
                    }
                Resposta.Exit(productes) as Resposta<List<Producte>>
            }
            .catch { error ->
                emit(Resposta.Fracas(error.message ?: "Error en obtenir els productes"))
            }

    override suspend fun obtenProducte(id: String): Resposta<Producte> {
        var producte: Producte = Producte()
        try {
            val refProductes = db.firestoreDb.collection(db.PRODUCTES)
            val consulta = refProductes.document(id)
            val doc = consulta.get().await()
            producte = doc.toObject<Producte>() ?: Producte()
        } catch (e: Exception) {
            return Resposta.Fracas(e.message ?: "Ha fallat en obtenir el producte")
        }
        return Resposta.Exit(producte)
    }

    override suspend fun obtenProductesDeCategoria(idCategoria: String): Flow<Resposta<List<Producte>>> =
        db.firestoreDb.collection(db.PRODUCTES)
            .whereEqualTo("idCategoria", idCategoria)
            .snapshots()
            .map { querySnapshot ->
                val productes = querySnapshot.documents
                    .mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject(Producte::class.java)
                    }
                Resposta.Exit(productes) as Resposta<List<Producte>>
            }
            .catch { error ->
                emit(Resposta.Fracas(error.message ?: "Error en obtenir els productes de la categoria"))
            }

    override suspend fun afegeixProducte(producte: Producte): Resposta<Boolean> {
        var existeix = false
        try {
            val resposta = existeixProducte(producte.nom, producte.idCategoria)
            if (resposta is Resposta.Exit) {
                existeix = resposta.dades
            } else if (resposta is Resposta.Fracas) {
                throw Exception(resposta.missatgeError)
            }
            if (!existeix) {
                val refProductes = db.firestoreDb.collection(db.PRODUCTES)
                val refProducteNou = refProductes.document()
                val producteAmbId = producte.copy(id = refProducteNou.id)
                refProducteNou.set(producteAmbId).await()
            }
        } catch (e: Exception) {
            return Resposta.Fracas(e.message ?: "Error afegint un producte nou")
        }
        return Resposta.Exit(!existeix)
    }

    override suspend fun eliminaProducte(id: String): Resposta<Boolean> {
        try {
            val refProductes = db.firestoreDb.collection(db.PRODUCTES)
            val refProducte = refProductes.document(id)
            refProducte.delete().await()
        } catch (e: Exception) {
            return Resposta.Fracas(e.message ?: "Ha fallat en eliminar el producte")
        }
        return Resposta.Exit(true)
    }

    override suspend fun actualitzaProducte(producte: Producte): Resposta<Boolean> {
        var actualitzat = false
        try {
            val refProductes = db.firestoreDb.collection(db.PRODUCTES)
            val refProducte = refProductes.document(producte.id)
            refProducte.set(producte).await()
            actualitzat = true
        } catch (e: Exception) {
            return Resposta.Fracas(e.message ?: "Ha fallat en actualitzar el producte")
        }
        return Resposta.Exit(actualitzat)
    }

    override suspend fun existeixProducte(nom: String, idCategoria: String): Resposta<Boolean> {
        try {
            val refProductes = db.firestoreDb.collection(db.PRODUCTES)
            val consulta = refProductes
                .whereEqualTo("nom", nom)
                .whereEqualTo("idCategoria", idCategoria)
            val noExisteix = consulta
                .get()
                .await()
                .documents.isEmpty()
            return Resposta.Exit(!noExisteix)
        } catch (e: Exception) {
            return Resposta.Fracas(e.message ?: "Ha fallat en comprovar l'existència del producte")
        }
    }
}