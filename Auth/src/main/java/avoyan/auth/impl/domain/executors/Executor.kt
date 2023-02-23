/**
 * Created by Michael Avoyan on 19/02/2023.
 */

package avoyan.auth.impl.domain.executors

import android.os.Looper

interface Executor {
    fun runOn(looper: Looper?, runnable: Runnable)
    fun runOnMainThread(runnable: Runnable)
    fun runOnBackgroundThread(runnable: Runnable)
    fun waitForTermination()
}