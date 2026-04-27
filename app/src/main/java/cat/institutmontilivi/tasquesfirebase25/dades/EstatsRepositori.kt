package cat.institutmontilivi.tasquesfirebase25.dades
import cat.institutmontilivi.tasquesfirebase25.model.app.Estat
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import kotlinx.coroutines.flow.Flow

interface EstatsRepositori {
    suspend fun obtenEstats(): Flow<Resposta<List<Estat>>>
    suspend fun afegeixEstat(estat: Estat): Resposta<Boolean>
    suspend fun eliminaEstat(id: String): Resposta<Boolean>
    suspend fun actualitzaEstat(estat: Estat): Resposta<Boolean>
    suspend fun existeixEstat(nom: String): Resposta<Boolean>
    suspend fun obtenEstat(id:String): Resposta<Estat>

}