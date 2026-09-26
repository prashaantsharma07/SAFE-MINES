package com.mine.governance.data.camera

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CameraHelper {

    fun createEvidencePhotoUri(context: Context): Pair<Uri, File> {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.cacheDir
        val imageFile = File.createTempFile(
            "MINE_EVIDENCE_${timeStamp}_",
            ".jpg",
            storageDir
        )
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        return Pair(uri, imageFile)
    }

    fun saveContentUriToEvidenceFile(context: Context, sourceUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val evidenceDir = File(context.filesDir, "evidence")
            if (!evidenceDir.exists()) evidenceDir.mkdirs()
            val destFile = File(evidenceDir, "EVIDENCE_${timeStamp}.jpg")
            destFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
