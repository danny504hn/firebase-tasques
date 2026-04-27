package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore

// Cal que els camps tinguin valor per defecte per evitar problemes en la deserialització de Firestore.
// Si no es proporcionen valors per defecte, Firestore pot llençar una excepció quan intenta crear
// una instància de l'objecte a partir de les dades emmagatzemades. Això és especialment important
// si els camps són de tipus no nul·la, ja que Firestore necessita assegurar-se que tots
// els camps tenen un valor vàlid per crear l'objecte correctament.
object usuariActual {
    var id: String = ""
    var correu: String = ""
    var nom: String = ""
    var usuarisHabituals: List<String> = listOf()
}