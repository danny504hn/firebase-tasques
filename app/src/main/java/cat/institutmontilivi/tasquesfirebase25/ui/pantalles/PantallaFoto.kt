package cat.institutmontilivi.tasquesfirebase25.ui.pantalles

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import cat.institutmontilivi.tasquesfirebase25.R
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation


@Preview
@Composable
fun PantallaFoto(
    urlDeLaImatge:String ="https://educacionplasticayvisual.com/wp-content/uploads/imagen-funcion-estetica.jpg",
    modifier: Modifier=Modifier,
    imageContentDescription: String = ""
) {
    // Variables d'estat mutables per mantenir l'escala i els valors del desplaçament
    var escala by remember { mutableStateOf(1f) }
    var deplasamentX by remember { mutableStateOf(0f) }
    var desplasamentY by remember { mutableStateOf(0f) }

    // Escala mínima i màxima en la que es pot fet el zoom
    val escalaMinima = 1f
    val escalaMaxima = 4f

    // Deplaçament inicial, abans de moure la imatge
    var desplasamentInicial by remember { mutableStateOf(Offset(0f, 0f)) }

    // Defineix el factor per alentir el moviment quan es deslaça la imatge
    val enlentimentDeMoviment = 0.5f

    // Componible Box que contindrà la imatge
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, desplasament, ampliacio, _ ->
                    // Actualitza l'escala amb el zoom
                    val escalaNova = escala * ampliacio
                    escala = escalaNova.coerceIn(escalaMinima, escalaMaxima)

                    // Calcula els nous deplasaments basant-se en l'ampliacio i el desplaçament
                    val centreX = size.width / 2
                    val centreY = size.height / 2
                    val canviDesplasamentX = (centreX - deplasamentX) * (escalaNova / escala - 1)
                    val canviDesplasamentY = (centreY - desplasamentY) * (escalaNova / escala - 1)

                    // Calcula els desplaçaments minim i maxim
                    val desplasamentMaximX = (size.width / 2) * (escala - 1)
                    val desplasamentMinimX = -desplasamentMaximX
                    val desplasamentMaximY = (size.height / 2) * (escala - 1)
                    val desplasamentMinimY = -desplasamentMaximY

                    // Actualitza els desplaçaments assegurant que són dintre del límits
                    if (escala * ampliacio <= escalaMaxima) {
                        deplasamentX = (deplasamentX + desplasament.x * escala * enlentimentDeMoviment + canviDesplasamentX)
                            .coerceIn(desplasamentMinimX, desplasamentMaximX)
                        desplasamentY = (desplasamentY + desplasament.y * escala * enlentimentDeMoviment + canviDesplasamentY)
                            .coerceIn(desplasamentMinimY, desplasamentMaximY)
                    }

                    // Desa el desplaçament inicial
                    if (desplasament != Offset(0f, 0f) && desplasamentInicial == Offset(0f, 0f)) {
                        desplasamentInicial = Offset(deplasamentX, desplasamentY)
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        // Reinici de l'escala i el desplaçament al fer doble tap
                        if (escala != 1f) {
                            escala = 1f
                            deplasamentX = desplasamentInicial.x
                            desplasamentY = desplasamentInicial.y
                        } else {
                            escala = 2f
                        }
                    }
                )
            }
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
                translationX = deplasamentX
                translationY = desplasamentY
            }
    ) {
        // Imatge on es mostrara el pinch to zoom

        val painter = rememberAsyncImagePainter(
            ImageRequest
                .Builder(LocalContext.current)
                .data(data = urlDeLaImatge)
                .apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                    transformations(RoundedCornersTransformation(topLeft = 20f, topRight = 20f, bottomLeft = 20f, bottomRight = 20f))
                })
                .build()
        )

        Image(
            painter = painter,
            contentDescription = imageContentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )
    }
}

