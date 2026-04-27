package cat.institutmontilivi.tasquesfirebase25.model.app


/*
Els camps de la classe cal que siguin "var" o que tinguin un valor per defecte per tal que
es pugui fer la deserialització automàtica de Firestore.
 */
data class Estat(
    val id:String = "",
    val nom:String = "",
    val colorFons:String = "#FFFFFFFF",
    val colorText:String = "#FF000000",
)
