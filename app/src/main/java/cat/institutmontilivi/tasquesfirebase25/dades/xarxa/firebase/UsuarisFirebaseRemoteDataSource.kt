package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase

import cat.institutmontilivi.tasquesfirebase25.dades.UsuarisRepositori
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import cat.institutmontilivi.tasquesfirebase25.model.app.Tasca
import cat.institutmontilivi.tasquesfirebase25.model.app.Usuari
import kotlinx.coroutines.runBlocking
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

    override suspend  fun eliminaUsuari(id: Usuari): Resposta<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend  fun modificaUsuari(usuari: Usuari): Resposta<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend  fun obtenTasquesUsuari(id: String): Resposta<List<Tasca>> {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override suspend fun afegeixTascaAUsuari(idTasca: String, idUsuari: String): Resposta<Boolean> {
        TODO("Not yet implemented")
    }
}