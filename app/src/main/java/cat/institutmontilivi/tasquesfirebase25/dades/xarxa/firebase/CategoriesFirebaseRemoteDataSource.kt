package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase

import cat.institutmontilivi.tasquesfirebase25.dades.CategoriesRepositori
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.model.app.Categoria
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CategoriesFirebaseRemoteDataSource(manegadorFirestore: ManegadorFirestore) : CategoriesRepositori {

    val db = manegadorFirestore


    override suspend fun obtenCategories(): Flow<Resposta<List<Categoria>>> = callbackFlow {
        val subscripcio = db.firestoreDb.collection(db.CATEGORIES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resposta.Fracas(error.message ?: "Error Firestore"))
                    return@addSnapshotListener
                }
                val llista = snapshot?.toObjects(Categoria::class.java) ?: emptyList()
                trySend(Resposta.Exit(llista))
            }
        awaitClose { subscripcio.remove() }
    }

    override suspend fun obtenCategoria(id: String): Resposta<Categoria> {
        return try {
            val document = db.firestoreDb.collection(db.CATEGORIES).document(id).get().await()
            val categoria = document.toObject(Categoria::class.java) ?: Categoria()
            Resposta.Exit(categoria)
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error al obtenir la categoria")
        }
    }

    override suspend fun afegeixCategoria(categoria: Categoria): Resposta<Boolean> {
        return try {
            val existeix = existeixCategoria(categoria.nom)
            if (existeix is Resposta.Fracas) throw Exception(existeix.missatgeError)
            if ((existeix as Resposta.Exit).dades) return Resposta.Exit(false)

            val ref = db.firestoreDb.collection(db.CATEGORIES).document()
            val categoriaAmbId = categoria.copy(id = ref.id)
            ref.set(categoriaAmbId).await()
            Resposta.Exit(true)
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error al crear la categoria")
        }
    }

    override suspend fun actualitzaCategoria(categoria: Categoria): Resposta<Boolean> {
        return try {
            db.firestoreDb.collection(db.CATEGORIES).document(categoria.id).set(categoria).await()
            Resposta.Exit(true)
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error al actualitzar la categoria")
        }
    }

    // REGLA: No es pot esborrar una categoria que tingui productes
    override suspend fun eliminaCategoria(id: String): Resposta<Boolean> {
        return try {
            val teProductes = categoriaTeProductes(id)
            if (teProductes is Resposta.Fracas) throw Exception(teProductes.missatgeError)
            if ((teProductes as Resposta.Exit).dades)
                return Resposta.Fracas("No es pot eliminar una categoria que té productes")

            db.firestoreDb.collection(db.CATEGORIES).document(id).delete().await()
            Resposta.Exit(true)
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error al eliminar la categoria")
        }
    }

    override suspend fun existeixCategoria(nom: String): Resposta<Boolean> {
        return try {
            val query = db.firestoreDb.collection(db.CATEGORIES)
                .whereEqualTo("nom", nom)
                .get()
                .await()
            Resposta.Exit(!query.isEmpty)
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error al consultar nom")
        }
    }


    override suspend fun categoriaTeProductes(idCategoria: String): Resposta<Boolean> {
        return try {
            val document = db.firestoreDb.collection(db.CATEGORIES).document(idCategoria).get().await()
            val categoria = document.toObject(Categoria::class.java)
                ?: return Resposta.Fracas("No s'ha trobat la categoria")
            Resposta.Exit(categoria.llistaIdsProductes.isNotEmpty())
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error al comprovar si la categoria té productes")
        }
    }
}