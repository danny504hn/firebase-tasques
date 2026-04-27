package cat.institutmontilivi.tasquesfirebase25.dades.local

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

enum class TipusMultimedia {
    IMATGE,
    VIDEO,
    AUDIO}


/**
 * Crea un fitxer buit, sense contingut
 */
fun Context.creaUnFitxerMultimedia(format: TipusMultimedia): File {
    val moment = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    var nomFitxerMultimedia = ""

    if (format == TipusMultimedia.IMATGE){
        nomFitxerMultimedia = "JPEG_" + moment + "_"
        return java.io.File.createTempFile(
            nomFitxerMultimedia,
            ".jpg",
            externalCacheDir
        )
    }
    else if (format == TipusMultimedia.VIDEO){
        nomFitxerMultimedia = "MPEG_" + moment + "_"
        return java.io.File.createTempFile(
            nomFitxerMultimedia,
            ".mp4",
            externalCacheDir
        )
    }
    else // if (format == TipusMultimedia.AUDIO){
        nomFitxerMultimedia = "MP3_" + moment + "_"
    return java.io.File.createTempFile(
        nomFitxerMultimedia,
        ".mp3",
        externalCacheDir
    )
}