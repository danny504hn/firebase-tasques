package cat.institutmontilivi.tasquesfirebase25.model.app

data class Categoria (
    val id:String,
    val nom:String,
    val colorFons:String,
    val colorText:String,
    val usuaris:List<String>,
    val tasques:List<String>,
)

/*
Col·leccions a crear:

- Usuaris
- Tasques
- Llistes
- Estats

 Estats inicials:
  - Urgent
  - Endarrerit
  - Acabat
  - Iniciat
  - En procès
  - Acabant
  - En espera


 */