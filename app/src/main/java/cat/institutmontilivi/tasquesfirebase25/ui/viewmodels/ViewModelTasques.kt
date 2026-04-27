package cat.institutmontilivi.tasquesfirebase25.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import cat.institutmontilivi.tasquesfirebase25.dades.BBDDFactory
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.cloudstorage.manegadorCloudStorage
import cat.institutmontilivi.tasquesfirebase25.model.app.Estat
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import cat.institutmontilivi.tasquesfirebase25.model.app.Tasca
import cat.institutmontilivi.tasquesfirebase25.navegacio.DestinacioTasques
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewModelTasques(savedStateHandle: SavedStateHandle): ViewModel() {
    private var _tasques = MutableStateFlow<EstatTasques>(EstatTasques())
    val tasques: StateFlow<EstatTasques> = _tasques.asStateFlow()
    val tasquesRepositori = BBDDFactory.obtenRepositoriTasques(null, BBDDFactory.DatabaseType.FIREBASE)
    val estatsRepositori = BBDDFactory.obtenRepositoriEstats(null, BBDDFactory.DatabaseType.FIREBASE)
    var tascaActual = Tasca()


    init{
        val args = savedStateHandle.toRoute<DestinacioTasques>()
        _tasques.update { _tasques.value.copy(usuariActual = args.idUsuari) }
        obtenEstats()
        obtenTasques(args.idUsuari)
    }

    fun afegeixTasca(tasca: Tasca)
    {
        viewModelScope.launch (Dispatchers.IO){
            tasquesRepositori.afegeixTasca (tasca)
        }
    }

    fun obtenEstats()
    {
        viewModelScope.launch (Dispatchers.IO){
            val nousEstats = mutableMapOf<String, Estat>()
            estatsRepositori.obtenEstats().collect{
                    resposta ->
                if(resposta is Resposta.Exit)
                {

                    val dades = resposta.dades
                    for(estat in dades)
                    {
                        nousEstats.put(estat.id, estat)
                    }
                }
                _tasques.emit(
                    tasques.value.copy(
                        estaCarregant = false,
                        estats = nousEstats.toMap(),
                        esErroni = false,
                        missatgeError = ""
                    )
                )
            }
        }
    }

    fun obtenTasques(idUsuari:String = "")
    {
        viewModelScope.launch (Dispatchers.IO){
            _tasques.emit ( _tasques.value.copy(estaCarregant = true))
            tasquesRepositori.obtenTasques(idUsuari).collect{
                    resposta ->
                if(resposta is Resposta.Exit)
                {
                    val dades = resposta.dades
                    _tasques.emit(
                        tasques.value.copy(
                            estaCarregant = false,
                            tasques = dades,
                            esErroni = false,
                            missatgeError = ""
                        )
                    )
                }
                else if(resposta is Resposta.Fracas)
                {
                    _tasques.emit(
                        tasques.value.copy(
                            estaCarregant = false,
                            tasques = listOf(),
                            esErroni = true,
                            missatgeError = resposta.missatgeError
                        )
                    )
                }
            }
        }
    }
    fun eliminaTasca(id:String)
    {
        viewModelScope.launch (Dispatchers.IO){
            tasquesRepositori.eliminaTasca(id)
        }
    }

    fun actualitzaTasca(tasca: Tasca)
    {
        viewModelScope.launch (Dispatchers.IO){
            tasquesRepositori.actualitzaTasca(tasca)
        }
    }

    fun afegeixFoto(uri: Uri)
    {
        viewModelScope.launch {
            val url:String = manegadorCloudStorage.carregaImatge(tascaActual.id, uri.lastPathSegment.toString(), uri)
            val novaLlista: MutableList<String> = tascaActual.uriFotos.toMutableList()
            novaLlista.add(url)
            tascaActual = tascaActual.copy(uriFotos =  novaLlista.toList())
            actualitzaTasca(tascaActual)
        }
    }
    fun afegeixVideo(uri: Uri)
    {
        viewModelScope.launch {
            val url:String = manegadorCloudStorage.carregaVideo(tascaActual.id, uri.lastPathSegment.toString(), uri)
            val novaLlista: MutableList<String> = tascaActual.uriVideos.toMutableList()
            novaLlista.add(url)
            tascaActual = tascaActual.copy(uriVideos =  novaLlista.toList())
            actualitzaTasca(tascaActual)
        }
    }

    data class EstatTasques(
        val estaCarregant:Boolean = true,
        val tasques:List<Tasca> = listOf(),
        val estats: Map<String, Estat> = mapOf(),
        val usuariActual: String = "",
        val esErroni:Boolean = false,
        val missatgeError:String = ""
    )
}