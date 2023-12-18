package utils

object ResourceUtils {
    fun getResourceAsText(path: String): String? =
        object {}.javaClass.getResource(path)?.readText()

}