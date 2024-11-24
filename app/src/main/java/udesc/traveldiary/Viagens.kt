package udesc.traveldiary

class Viagens(
    val id: Int = 0,
    val nome: String = "",
    val data: String = "",
    val nota: String = "",
    val foto: String = ""
) {
    override fun toString(): String {
        return "$id. $nome, $data, $nota"
    }
}
