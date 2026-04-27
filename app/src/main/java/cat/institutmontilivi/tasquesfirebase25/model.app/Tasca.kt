package cat.institutmontilivi.tasquesfirebase25.model.app

data class Tasca (
    val id:String="",
    val titol:String="",
    val descripcio:String="",
    val dataLimit:Long=0,
    val estat:String="",
    val usuaris:List<String> =listOf(),
    val uriFotos:List<String> =listOf(),
    val uriVideos:List<String> =listOf(),
    val uriAudios:List<String> =listOf(),
)