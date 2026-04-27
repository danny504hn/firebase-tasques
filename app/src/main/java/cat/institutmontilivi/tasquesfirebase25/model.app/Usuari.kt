package cat.institutmontilivi.tasquesfirebase25.model.app

data class Usuari(
    val id:String="",
    val nom:String="",
    val cognom:String="",
    val correu:String="",
    val tasques:List<String> = listOf(),
    val usuarisHabituals:List<String> = listOf(),
)
