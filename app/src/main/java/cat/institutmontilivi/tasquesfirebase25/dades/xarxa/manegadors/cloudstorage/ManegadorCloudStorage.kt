package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.cloudstorage

import android.net.Uri
import com.google.firebase.Firebase

import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

import kotlinx.coroutines.tasks.await

object manegadorCloudStorage {
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    val PATH_FOTOS = "Fotos"
    val PATH_VIDEOS = "Videos"
    val PATH_AUDIOS = "Audios"

    private fun getStorageReferencePerTascaITipusArxiu(
        idTasca: String,
        tipusArxiu: String
    ): StorageReference {
        TODO("Not yet implemented")
    }

    private fun getStorageReferencePerTasca(idTasca: String): StorageReference {
        return storageRef.child(idTasca)
    }

    private suspend fun carregaArxiu(
        idTasca: String,
        tipusArxiu: String,
        nomArxiu: String,
        pathArxiu: Uri
    ): String {
        TODO("Not yet implemented")
    }

    suspend fun obtenLlistaImatgesPerTascaITipusArxiu(
        idTasca: String,
        tipusArxiu: String
    ): List<String> {
        TODO("Not yet implemented")
    }

    suspend fun carregaImatge(idTasca: String, nomArxiu: String, pathArxiu: Uri):String {
        TODO("Not yet implemented")
    }

    suspend fun carregaVideo(idTasca: String, nomArxiu: String, pathArxiu: Uri):String {
        TODO("Not yet implemented")
    }
}