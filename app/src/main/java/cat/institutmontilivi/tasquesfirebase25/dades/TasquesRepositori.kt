package cat.institutmontilivi.tasquesfirebase25.dades

import cat.institutmontilivi.tasquesfirebase25.model.app.Estat
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import cat.institutmontilivi.tasquesfirebase25.model.app.Tasca
import kotlinx.coroutines.flow.Flow

interface TasquesRepositori {
    suspend fun obtenTasques(idUsuari:String): Flow<Resposta<List<Tasca>>>
    suspend fun afegeixTasca(tasca: Tasca): Resposta<Boolean>
    suspend fun eliminaTasca(idTasca: String): Resposta<Boolean>
    suspend fun actualitzaTasca(tasca: Tasca): Resposta<Boolean>
    suspend fun existeixTasca(nom: String): Resposta<Boolean>
    suspend fun obtenTasca(idTasca:String): Resposta<Tasca>
    suspend fun convidaUsuari(idTasca:String, idUsuari: String): Resposta<Tasca>
}