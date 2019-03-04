package ke.co.appslab.smarthome.utils

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val databaseError: String) : Result<Nothing>()
}