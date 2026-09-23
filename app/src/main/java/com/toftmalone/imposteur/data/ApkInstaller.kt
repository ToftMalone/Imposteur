package com.toftmalone.imposteur.data

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * Hands a downloaded APK to Android's own installer, which asks the player to
 * confirm. The app never installs anything silently.
 */
object ApkInstaller {

    /** Where updates are downloaded; must match res/xml/update_paths.xml. */
    fun downloadDirectory(context: Context): File = File(context.cacheDir, "updates")

    /**
     * False on Android 8+ until the player has let Imposteur install apps.
     * Android asks for it by itself on the first install, so this only picks
     * the hint the update window shows.
     */
    fun canInstallPackages(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.packageManager.canRequestPackageInstalls()

    /** Opens the system installer on [apk]. False when no installer could be started. */
    fun install(context: Context, apk: File): Boolean = runCatching {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.updates", apk)
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri, "application/vnd.android.package-archive")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }.isSuccess
}
