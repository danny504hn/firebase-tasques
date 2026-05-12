package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase

import cat.institutmontilivi.tasquesfirebase25.dades.UsuarisRepositori
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import cat.institutmontilivi.tasquesfirebase25.model.app.Tasca
import cat.institutmontilivi.tasquesfirebase25.model.app.Usuari
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await


//https://medium.com/firebase-tips-tricks/how-to-read-data-from-cloud-firestore-using-get-bf03b6ee4953

class UsuarisFirebaseRemoteDataSource ( manegadorFirestore: ManegadorFirestore): UsuarisRepositori {
    val db = manegadorFirestore

    override suspend  fun obtenUsuaris(): Resposta<List<Usuari>> {
        TODO("Not yet implemented")
    }

    override suspend fun obtenUsuari(correu: String): Resposta<Usuari> {
        var usuari: Usuari = Usuari()
        try {
            val refUsuaris = db.firestoreDb.collection(db.USUARIS)
            val consulta = refUsuaris.whereEqualTo("correu", correu)
            if ( consulta.get().await().documents.isEmpty())
                throw Exception("No hi ha cap usuari amb el correu $correu")
            usuari = consulta.get().await().documents.first().toObject(Usuari::class.java)!!
        }catch (e: Exception) {
            Resposta.Fracas(e.message ?: "Error cercant l'usuari $correu" )
        }
        return Resposta.Exit(usuari)
    }

    override suspend  fun afegeixUsuari(usuari: Usuari): Resposta<Boolean> {
        try {
            val resposta = existeixUsuari(usuari.correu)
            if(resposta is Resposta.Fracas)
                throw Exception(resposta.missatgeError)

            val jaExistia = (resposta as Resposta.Exit).dades
            if(!jaExistia){
                val refUsuaris = db.firestoreDb.collection(db.USUARIS)
                val refUsuariNou = refUsuaris.document()
                val usuariNou = usuari.copy(id = refUsuariNou.id)
                refUsuariNou.set(usuariNou).await()
                return Resposta.Exit(true)
            }
            else{
                return Resposta.Exit(dades = false)
            }

        } catch (e : Exception){
            return Resposta.Fracas(e.message ?: "Error en afegir l'usuari")
        }
    }

    override suspend fun eliminaUsuari(id: Usuari): Resposta<Boolean> {
        try {
            val refUsuaris = db.firestoreDb.collection(db.USUARIS)
            val refUsuari = refUsuaris.document(id.id)
            refUsuari.delete().await()
        } catch (e: Exception) {
            return Resposta.Fracas(e.message ?: "Ha fallat en eliminar l'usuari")
        }
        return Resposta.Exit(true)
    }

    override suspend fun modificaUsuari(usuari: Usuari): Resposta<Boolean> {
        var actualitzat = false
        try {
            val refUsuaris = db.firestoreDb.collection(db.USUARIS)
            val refUsuari = refUsuaris.document(usuari.id)
            refUsuari.set(usuari).await()
            actualitzat = true
        } catch (e: Exception) {
            return Resposta.Fracas(e.message ?: "Ha fallat en modificar l'usuari")
        }
        return Resposta.Exit(actualitzat)
    }

    override suspend fun obtenTasquesUsuari(id: String): Resposta<List<Tasca>> {
        try {
            val refUsuaris = db.firestoreDb.collection(db.USUARIS)
            val doc = refUsuaris.document(id).get().await()
            val usuari = doc.toObject<Usuari>() ?: return Resposta.Fracas("No s'ha trobat l'usuari")
            val tasques = usuari.tasques.mapNotNull { idTasca ->
                val refTasca = db.firestoreDb.collection(db.TASQUES).document(idTasca)
                refTasca.get().await().toObject(Tasca::class.java)
            }
            return Resposta.Exit(tasques)
        } catch (e: Exception) {
            return Resposta.Fracas(e.message ?: "Ha fallat en obtenir les tasques de l'usuari")
        }
    }

    override suspend fun existeixUsuari(correu: String): Resposta<Boolean> {
        try{
            val documents = db.firestoreDb.collection(db.USUARIS)
                .whereEqualTo("correu",correu)
                .get().await()
            return Resposta.Exit(!documents.isEmpty)
        }catch (e : Exception){
            return Resposta.Fracas(e.message ?: "Error en consultar l'existencia de l'usuari")
        }
    }

    override suspend fun eliminaTascaDeUsuari(idTasca: String, idUsuari: String): Resposta<Boolean> {
        try {
            val refUsuari = db.firestoreDb.collection(db.USUARIS).document(idUsuari)
            refUsuari.update("tasques", FieldValue.arrayRemove(idTasca)).await()
        } catch (e: Exception) {
            return Resposta.Fracas(e.message ?: "Ha fallat en eliminar la tasca de l'usuari")
        }
        return Resposta.Exit(true)
    }

    override suspend fun afegeixTascaAUsuari(idTasca: String, idUsuari: String): Resposta<Boolean> {
        try {
            val refUsuari = db.firestoreDb.collection(db.USUARIS).document(idUsuari)
            refUsuari.update("tasques", FieldValue.arrayUnion(idTasca)).await()
        } catch (e: Exception) {
            return Resposta.Fracas(e.message ?: "Ha fallat en afegir la tasca a l'usuari")
        }
        return Resposta.Exit(true)
    }
}