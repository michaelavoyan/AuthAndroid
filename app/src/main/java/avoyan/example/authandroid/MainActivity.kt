package avoyan.example.authandroid

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import avoyan.auth.api.AuthProvider
import avoyan.auth.api.entities.AuthConfig
import avoyan.example.authandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.simpleName

    private lateinit var binding: ActivityMainBinding

    private val auth = AuthProvider.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAuthenticate.setOnClickListener {
            auth.isAuthenticationAvailable(
                context = this,
                successHandler = { isAuthenticationAvailable ->
                    Log.d(TAG, "VCL isAuthenticationAvailable: $isAuthenticationAvailable")
                    if (isAuthenticationAvailable) {
                        auth.authenticate(
                            activity = this,
                            authConfig = AuthConfig(
                                title = "The passcode you use to unlock this Phone, can also be used to access your Velocity account."
                            ),
                            successHandler = { isRecognized ->
                                Log.d(TAG, "VCL User recognized: $isRecognized")
                            },
                            errorHandler = { error ->
                                Log.e(TAG, "VCL Auth error: $error")
                                showAlert("Auth error", error.message ?: "")
                            })
                    } else {
                        showAlert("Authentication is NOT Available","")
                    }
                },
                errorHandler = { error ->
                    Log.e(TAG, "VCL isAuthenticationAvailable error: $error")
                    showAlert("isAuthenticationAvailable error", error.message ?: "")
                })
        }

        binding.btnOpenSecuritySettings.setOnClickListener {
            auth.openSecuritySettings(
                this,
                successHandler = { isOpen ->
                    Log.d(TAG, "VCL Security settings open: $isOpen")
                },
                errorHandler = { error ->
                    Log.e(TAG, "VCL Security settings open error: $error")
                })
        }
    }

    private fun showAlert(title: String, message: String) {
        runOnUiThread{
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                    "OK"
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}