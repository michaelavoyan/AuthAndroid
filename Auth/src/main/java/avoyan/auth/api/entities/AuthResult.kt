/**
 * Created by Michael Avoyan on 19/02/2023.
 */

package avoyan.auth.api.entities

data class AuthError(
    val description: String? = null,
    val code: Int? = null
): Error(description)

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val authError: AuthError) : Result<Nothing>()
}

fun <T> Result<T>.handleResult(successHandler:(d: T) -> Unit, errorHandler: (authError: AuthError) -> Unit) {
    when (this) {
        is Result.Success -> {
            successHandler(this.data)
        }
        is Result.Failure -> {
            errorHandler(this.authError)
        }
    }
}

val <T> Result<T>.data: T?
    get() = (this as? Result.Success)?.data
