package cat.institutmontilivi.tasquesfirebase25.model.app

data class Categoria(
    val id: String = "",
    val nom: String = "",
    val llistaIdsProductes: List<String> = emptyList()
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