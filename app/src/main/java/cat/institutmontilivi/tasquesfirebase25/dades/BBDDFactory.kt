package cat.institutmontilivi.tasquesfirebase25.dades

import android.content.Context
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase.CategoriesFirebaseRemoteDataSource
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase.EstatsFirebaseRemoteDataSource
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase.ProductesFirebaseRemoteDataSource
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase.TasquesFirebaseRemoteDataSource
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.firebase.UsuarisFirebaseRemoteDataSource
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.firestore.ManegadorFirestore


object BBDDFactory
{
    enum class DatabaseType {
        FIREBASE
    }


    fun obtenRepositoriUsuaris(context: Context?, type: DatabaseType): UsuarisRepositori {
        return when (type) {
            DatabaseType.FIREBASE -> UsuarisFirebaseRemoteDataSource(ManegadorFirestore())
        }
    }

    fun obtenRepositoriEstats(context: Context?, type: DatabaseType): EstatsRepositori {
        return when (type) {
            DatabaseType.FIREBASE -> EstatsFirebaseRemoteDataSource(ManegadorFirestore())
        }
    }

    fun obtenRepositoriTasques(context: Context?, type: DatabaseType): TasquesRepositori {
        return when (type) {
            DatabaseType.FIREBASE -> TasquesFirebaseRemoteDataSource(ManegadorFirestore())
        }
    }

    fun obtenRepositoriCategories(context: Context?, type: DatabaseType): CategoriesRepositori {
        return when (type) {
            DatabaseType.FIREBASE -> CategoriesFirebaseRemoteDataSource(ManegadorFirestore())
        }
    }

    fun obtenRepositoriProductes(context: Context?, type: DatabaseType): ProductesRepositori {
        return when (type) {
            DatabaseType.FIREBASE -> ProductesFirebaseRemoteDataSource(ManegadorFirestore())
        }
    }
}