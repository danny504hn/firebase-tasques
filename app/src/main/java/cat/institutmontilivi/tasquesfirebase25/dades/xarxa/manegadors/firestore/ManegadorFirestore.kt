package cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore

import cat.institutmontilivi.tasquesfirebase25.model.app.Usuari
import com.google.firebase.firestore.FirebaseFirestore

class ManegadorFirestore {
    public val USUARIS = "Usuaris"
    public val ESTATS = "Estats"
    public val TASQUES = "Tasques"
    val CATEGORIES = "categories"
    val PRODUCTES = "productes"
    public val firestoreDb = FirebaseFirestore.getInstance()
}