/**
 * Created by Michael Avoyan on 19/02/2023.
 */

package avoyan.auth.api

import avoyan.auth.impl.AuthImpl
import avoyan.auth.impl.data.executors.ExecutorImpl

class AuthProvider {
    companion object {
        fun instance(): Auth {
            return AuthImpl(ExecutorImpl())
        }
    }
}