package cat.institutmontilivi.tasquesfirebase25.dades

import cat.institutmontilivi.tasquesfirebase25.model.app.Producte
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import kotlinx.coroutines.flow.Flow
interface ProductesRepositori {

    suspend fun obtenProductes(): Flow<Resposta<List<Producte>>>

    suspend fun obtenProducte(id: String): Resposta<Producte>

    suspend fun obtenProductesDeCategoria(idCategoria: String): Flow<Resposta<List<Producte>>>

    suspend fun afegeixProducte(producte: Producte): Resposta<Boolean>

    suspend fun eliminaProducte(id: String): Resposta<Boolean>

    suspend fun actualitzaProducte(producte: Producte): Resposta<Boolean>

    suspend fun existeixProducte(nom: String, idCategoria: String): Resposta<Boolean>
}