package cat.institutmontilivi.tasquesfirebase25.ui.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.institutmontilivi.tasquesfirebase25.dades.BBDDFactory
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase.DadesFake
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore
import cat.institutmontilivi.tasquesfirebase25.model.app.Estat
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class ViewModelEstats :ViewModel() {
    private var _estat = MutableStateFlow<EstatEstats>(EstatEstats())
    val estat: StateFlow<EstatEstats> = _estat.asStateFlow()
    val estatsRepositori = BBDDFactory.obtenRepositoriEstats(null, BBDDFactory.DatabaseType.FIREBASE)

    init{
       obtenEstats()
    }

    fun afegeixEstat(estat: Estat)
    {
        viewModelScope.launch (Dispatchers.IO){

            estatsRepositori.afegeixEstat (estat)
        }
    }



fun obtenEstats()
{
    viewModelScope.launch (Dispatchers.IO){
        _estat.emit ( _estat.value.copy(estaCarregant = true))
        estatsRepositori.obtenEstats().collect{
                resposta ->

            if(resposta is Resposta.Exit)
            {
                android.util.Log.d("ESTATS", "Dades: ${resposta.dades}")
                val dades = resposta.dades
                _estat.emit(
                    estat.value.copy(
                        estaCarregant = false,
                        estats = dades,
                        esErroni = false,
                        missatgeError = ""
                    )
                )
            }
            else if(resposta is Resposta.Fracas)
            {
                _estat.emit(
                    estat.value.copy(
                        estaCarregant = false,
                        estats = listOf(),
                        esErroni = true,
                        missatgeError = resposta.missatgeError
                    )
                )
            }
        }
    }
}
    fun eliminaEstat(id:String)
    {
        viewModelScope.launch (Dispatchers.IO){
            estatsRepositori.eliminaEstat(id)
        }
    }
    fun generaEstatsFake() {
        viewModelScope.launch(Dispatchers.IO) {
            val estatsRepositori = BBDDFactory.obtenRepositoriEstats(null, BBDDFactory.DatabaseType.FIREBASE)
            listOf(
                Estat(nom = "Pendent", colorFons = "#FFFF9800", colorText = "#FF000000"),
                Estat(nom = "En curs", colorFons = "#FF2196F3", colorText = "#FFFFFFFF"),
                Estat(nom = "Fet", colorFons = "#FF4CAF50", colorText = "#FFFFFFFF")
            ).forEach { estatsRepositori.afegeixEstat(it) }
        }
    }
    fun actualitzaEstat(estat:Estat)
    {
        viewModelScope.launch (Dispatchers.IO){
            estatsRepositori.actualitzaEstat(estat)
        }
    }
    val categoriesRepositori = BBDDFactory.obtenRepositoriCategories(null, BBDDFactory.DatabaseType.FIREBASE)
    val productesRepositori = BBDDFactory.obtenRepositoriProductes(null, BBDDFactory.DatabaseType.FIREBASE)

    fun generaDadesFake() {
        viewModelScope.launch(Dispatchers.IO) {
            DadesFake(ManegadorFirestore()).generaDadesFake()
        }
    }

    data class EstatEstats(
        val estaCarregant:Boolean = true,
        val estats:List<Estat> = listOf(),
        val esErroni:Boolean = false,
        val missatgeError:String = ""
    )
}