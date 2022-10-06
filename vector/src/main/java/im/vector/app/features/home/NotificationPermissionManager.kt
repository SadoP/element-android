/*
 * Copyright (c) 2022 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.features.home

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import im.vector.app.R
import im.vector.app.core.utils.checkPermissions
import im.vector.app.features.settings.VectorPreferences
import org.matrix.android.sdk.api.util.BuildVersionSdkIntProvider
import javax.inject.Inject

class NotificationPermissionManager @Inject constructor(
        private val sdkIntProvider: BuildVersionSdkIntProvider,
        private val vectorPreferences: VectorPreferences,
) {

    fun isPermissionGranted(activity: Activity): Boolean {
        return if (sdkIntProvider.isAtLeast(Build.VERSION_CODES.TIRAMISU)) {
            ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // No notification permission management before API 33.
            true
        }
    }

    fun eventuallyRequestPermission(
            activity: Activity,
            requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
            showRationale: Boolean = true,
            ignorePreference: Boolean = false,
    ) {
        if (!sdkIntProvider.isAtLeast(Build.VERSION_CODES.TIRAMISU)) return
        if (!vectorPreferences.areNotificationEnabledForDevice() && !ignorePreference) return
        checkPermissions(
                listOf(Manifest.permission.POST_NOTIFICATIONS),
                activity,
                activityResultLauncher = requestPermissionLauncher,
                if (showRationale) R.string.permissions_rationale_msg_notification else 0
        )
    }

    @RequiresApi(33)
    fun askPermission(requestPermissionLauncher: ActivityResultLauncher<Array<String>>) {
        requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        )
    }
}
