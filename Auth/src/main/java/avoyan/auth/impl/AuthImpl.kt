/**
 * Created by Michael Avoyan on 19/02/2023.
 */

package avoyan.auth.impl

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import avoyan.auth.api.Auth
import avoyan.auth.api.entities.AuthConfig
import avoyan.auth.api.entities.AuthError
import avoyan.auth.impl.domain.executors.Executor
import java.lang.Exception

class AuthImpl(
    private val executor: Executor
): Auth {

    private val allowedAuthenticators = BiometricManager.Authenticators.BIOMETRIC_WEAK or
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL

    override fun isAuthenticationAvailable(
        context: Context,
        successHandler: (Boolean) -> Unit,
        errorHandler: (AuthError) -> Unit
    ) {
        executor.runOnMainThread {
            try {
                successHandler(hasBiometricCapability(context) == BiometricManager.BIOMETRIC_SUCCESS)
            } catch (e: Exception) {
                errorHandler(AuthError(e.message))
            }
        }
    }

    override fun authenticate(
        activity: FragmentActivity,
        authConfig: AuthConfig,
        successHandler: (Boolean) -> Unit,
        errorHandler: (AuthError) -> Unit
    ) {
        executor.runOnMainThread {
            try {
                // Prepare BiometricPrompt Dialog
                val promptInfo = setBiometricPromptInfo(
                    title = authConfig.title,
                    subtitle = authConfig.subtitle,
                    description = authConfig.description,
                    isConfirmationRequired = authConfig.isConfirmationRequired
                )

                // Attach with caller and callback handler
                val biometricPrompt = initBiometricPrompt(activity, successHandler, errorHandler)

                // Authenticate with a CryptoObject if provided, otherwise default authentication
                biometricPrompt.apply {
                    authConfig.cryptoObject?.let { cryptoObject ->
                        authenticate(promptInfo, cryptoObject)
                    } ?: authenticate(promptInfo)
                }
            } catch (e: Exception) {
                errorHandler(AuthError(e.message))
            }
        }
    }

    override fun openSecuritySettings(
        context: Context,
        successHandler: (Boolean) -> Unit,
        errorHandler: (AuthError) -> Unit
    ) {
        executor.runOnMainThread {
            try {
                ActivityCompat.startActivity(
                    context,
                    Intent(
                        Settings.ACTION_SECURITY_SETTINGS
                    ),
                    null
                )
                successHandler(true)
            } catch (e: Exception) {
                errorHandler(AuthError(e.message))
            }
        }
    }

    /**
     * Checks if the device has Biometric support
     */
    private fun hasBiometricCapability(context: Context): Int {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(allowedAuthenticators)
    }

    /**
     * Prepares PromptInfo dialog with provided configuration
     */
    private fun setBiometricPromptInfo(
        title: String,
        subtitle: String,
        description: String,
        isConfirmationRequired: Boolean
    ): BiometricPrompt.PromptInfo {
        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)

        // Use Device Credentials if allowed, otherwise show Cancel Button
        builder.apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                setDeviceCredentialAllowed(true)
            } else {
                setAllowedAuthenticators(allowedAuthenticators)
            }
//            setNegativeButtonText(cancelTitle)
            setConfirmationRequired(isConfirmationRequired)
        }
        return builder.build()
    }

    /**
     * Initializes BiometricPrompt with the caller and callback handlers
     */
    private fun initBiometricPrompt(
        activity: FragmentActivity,
        successHandler: (Boolean) -> Unit,
        errorHandler: (AuthError) -> Unit
    ): BiometricPrompt {
        // Attach calling Activity
        val executor = ContextCompat.getMainExecutor(activity)

        // Attach callback handlers
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                errorHandler(AuthError(errString.toString(), errorCode))
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                successHandler(false)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                successHandler(true)
            }
        }

        return BiometricPrompt(activity, executor, callback)
    }
}