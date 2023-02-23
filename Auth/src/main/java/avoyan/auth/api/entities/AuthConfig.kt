/**
 * Created by Michael Avoyan on 19/02/2023.
 */

package avoyan.auth.api.entities

import androidx.biometric.BiometricPrompt

data class AuthConfig(
    val title: String,
    val subtitle: String = "",
    val description: String = "",
    val cryptoObject: BiometricPrompt.CryptoObject? = null,
    val isConfirmationRequired: Boolean = true
)