package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase

import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.model.app.Categoria
import cat.institutmontilivi.tasquesfirebase25.model.app.Producte
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import kotlinx.coroutines.tasks.await

/**
 * Genera 6 categories amb 6 productes cadascuna.
 * Cada cop que es crida, RESTAURA les dades:
 *   - Si la categoria no existia → la crea amb els productes
 *   - Si la categoria ja existia → esborra els productes antics i els recrea
 */
class DadesFake(manegadorFirestore: ManegadorFirestore) {

    val db = manegadorFirestore

    private val dadesFake = mapOf(
        "Fruites"  to listOf("Poma", "Pera", "Plàtan", "Maduixa", "Raïm", "Taronja"),
        "Verdures" to listOf("Tomàquet", "Enciam", "Ceba", "Pastanaga", "Albergínia", "Pebrot"),
        "Làctics"  to listOf("Llet", "Iogurt", "Mantega", "Formatge", "Nata", "Quark"),
        "Carns"    to listOf("Pollastre", "Vedella", "Porc", "Xai", "Gall dindi", "Conill"),
        "Peixos"   to listOf("Salmó", "Tonyina", "Bacallà", "Sardina", "Lluç", "Rap"),
        "Cereals"  to listOf("Pa", "Arròs", "Pasta", "Civada", "Blat de moro", "Quinoa")
    )

    suspend fun generaDadesFake(): Resposta<Boolean> {
        return try {
            dadesFake.forEach { (nomCategoria, nomsProductes) ->
                restauraCategoria(nomCategoria, nomsProductes)
            }
            Resposta.Exit(true)
        } catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error en generar les dades fake")
        }
    }

    private suspend fun restauraCategoria(nomCategoria: String, nomsProductes: List<String>) {
        val refCategories = db.firestoreDb.collection(db.CATEGORIES)
        val query = refCategories.whereEqualTo("nom", nomCategoria).get().await()

        val idCategoria: String
        val llistaIdsProductesNova = mutableListOf<String>()

        if (query.isEmpty) {
            // La categoria NO existeix → reservem l'id primer
            val refCategoriaNova = refCategories.document()
            idCategoria = refCategoriaNova.id

            // Creem els productes
            nomsProductes.forEach { nomProducte ->
                val ref = db.firestoreDb.collection(db.PRODUCTES).document()
                ref.set(Producte(id = ref.id, nom = nomProducte, idCategoria = idCategoria)).await()
                llistaIdsProductesNova.add(ref.id)
            }

            // Creem la categoria amb la llista de productes
            refCategoriaNova.set(
                Categoria(id = idCategoria, nom = nomCategoria, llistaIdsProductes = llistaIdsProductesNova)
            ).await()

        } else {
            // La categoria JA existeix → restaurem
            val docCategoria = query.documents.first()
            idCategoria = docCategoria.id
            val categoriaExistent = docCategoria.toObject(Categoria::class.java)!!

            // Esborrem els productes antics
            categoriaExistent.llistaIdsProductes.forEach { idProducteAntiga ->
                db.firestoreDb.collection(db.PRODUCTES).document(idProducteAntiga).delete().await()
            }

            // Creem els productes nous
            nomsProductes.forEach { nomProducte ->
                val ref = db.firestoreDb.collection(db.PRODUCTES).document()
                ref.set(Producte(id = ref.id, nom = nomProducte, idCategoria = idCategoria)).await()
                llistaIdsProductesNova.add(ref.id)
            }

            // Actualitzem la categoria amb la nova llista
            db.firestoreDb.collection(db.CATEGORIES).document(idCategoria)
                .set(categoriaExistent.copy(llistaIdsProductes = llistaIdsProductesNova)).await()
        }
    }
}