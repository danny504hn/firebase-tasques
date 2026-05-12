package cat.institutmontilivi.tasquesfirebase25.dades

import cat.institutmontilivi.tasquesfirebase25.model.app.Categoria
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import kotlinx.coroutines.flow.Flow

interface CategoriesRepositori {

    suspend fun obtenCategories(): Flow<Resposta<List<Categoria>>>

    suspend fun obtenCategoria(id: String): Resposta<Categoria>

    suspend fun afegeixCategoria(categoria: Categoria): Resposta<Boolean>

    suspend fun eliminaCategoria(id: String): Resposta<Boolean>

    suspend fun actualitzaCategoria(categoria: Categoria): Resposta<Boolean>

    suspend fun existeixCategoria(nom: String): Resposta<Boolean>

    suspend fun categoriaTeProductes(idCategoria: String): Resposta<Boolean>
}