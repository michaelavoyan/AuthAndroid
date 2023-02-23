/**
 * Created by Michael Avoyan on 19/02/2023.
 */

package avoyan.auth.api

import android.content.Context
import androidx.fragment.app.FragmentActivity
import avoyan.auth.api.entities.AuthConfig
import avoyan.auth.api.entities.AuthError

interface Auth {
    /**
     * Checks if authentication is available on the device
     */
    fun isAuthenticationAvailable(
        context: Context,
        successHandler: (Boolean) -> Unit,
        errorHandler: (AuthError) -> Unit
    )

    /**
     * Displays a authentication identification dialog with provided configurations
     */
    fun authenticate(
        activity: FragmentActivity,
        authConfig: AuthConfig,
        successHandler: (Boolean) -> Unit,
        errorHandler: (AuthError) -> Unit
    )

    /**
     * Navigates to device's security settings screen for authentication setup
     */
    fun openSecuritySettings(
        context: Context,
        successHandler: (Boolean) -> Unit,
        errorHandler: (AuthError) -> Unit
    )
}