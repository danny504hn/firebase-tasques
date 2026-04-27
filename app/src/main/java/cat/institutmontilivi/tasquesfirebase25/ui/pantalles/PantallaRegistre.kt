package cat.institutmontilivi.tasquesfirebase25.ui.pantalles

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.analitiques.ManegadorAnalitiques
import cat.institutmontilivi.tasquesfirebase25.dades.xarxa.manegadors.autentificacio.ManegadorAutentificacio
import cat.institutmontilivi.tasquesfirebase25.model.app.Resposta
import kotlinx.coroutines.launch

@Preview
@Composable
fun PreviewRegistre()
{
    PantallaRegistre(
        manegadorAnalitiques= ManegadorAnalitiques((LocalContext.current)),
        ManegadorAutentificacio(LocalContext.current),
        navegaEnrera = {}) {}
}

@Composable
fun PantallaRegistre(
    manegadorAnalitiques: ManegadorAnalitiques,
    manegadorAutentificacio: ManegadorAutentificacio,
    navegaEnrera: () -> Unit,
    navegaAInici: () -> Unit
)
{
    val context = LocalContext.current
    var correu by remember { mutableStateOf("") }
    var motDePas by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var missatgeError by remember { mutableStateOf("") }
    val ambit = rememberCoroutineScope()

    manegadorAnalitiques.registraVisitaAPantalla("Registre")
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crea un compte",
            style = MaterialTheme.typography.displayMedium,
            color= MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(48.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp, 0.dp, 48.dp, 0.dp),
            label = { Text(text = "Correu electrònic") },
            value = correu,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = { correu = it })

        Spacer(modifier = Modifier.height(24.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp, 0.dp, 48.dp, 0.dp),
            label = { Text(text = "Mot de pas") },
            value = motDePas,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { motDePas = it })

        Spacer(modifier = Modifier.height(32.dp))
        if(error)
        {
            Text(text = missatgeError, style = TextStyle(color = MaterialTheme.colorScheme.error))
        }
        else {
            Text(text = "-------- o --------", style = TextStyle(color = Color.Gray))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(48.dp, 0.dp, 48.dp, 0.dp),
            onClick = {
                ambit.launch {
                    registra(correu, motDePas, manegadorAutentificacio,  context, navegaAInici, navegaEnrera)
                }
            },
            shape = RoundedCornerShape(50.dp),
        ) {
            Text(text = "Registra't")
        }
        Spacer(modifier = Modifier.height(48.dp))
        ClickableText(
            text = AnnotatedString("Tens un compte? Inicia la sessió"),
            onClick = {
                navegaEnrera()
            },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }

}


private suspend fun registra(
    correu: String,
    motDePas: String,
    autentificacio: ManegadorAutentificacio,
    context: Context,
    navegaAInici: () -> Unit,
    navegaEnrera: () -> Unit
): Pair<Boolean, String>{
    lateinit var resultat: Pair<Boolean, String>
    if(correu.isNotEmpty() && motDePas.isNotEmpty()) {
        when(val resposta = autentificacio.creaUsuariAmbCorreuIMotDePas(correu, motDePas)) {
            is Resposta.Exit -> {
                resultat = Pair(false,"")
                when (val respostaLogin = autentificacio.iniciaSessioAmbCorreuIMotDePas(correu, motDePas))
                {
                    is Resposta.Exit->{navegaAInici()}
                    is Resposta.Fracas ->{navegaEnrera}
                }
                navegaAInici()
            }
            is Resposta.Fracas -> {
                resultat = Pair(true,resposta.missatgeError)
            }
        }
    } else {
        resultat = Pair(true, "Cal omplir tots els camps")
    }
    return resultat
}