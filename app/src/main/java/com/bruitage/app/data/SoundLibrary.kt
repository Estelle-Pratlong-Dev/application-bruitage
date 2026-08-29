package com.bruitage.app.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile
import java.io.File

/**
 * Les sons ne sont plus embarqués dans l'APK : ils vivent dans le dossier privé de
 * l'appli sur le stockage externe (aucune permission requise, disponible sur toutes
 * les versions d'Android). L'utilisateur les ajoute via le sélecteur "Parcourir" dans
 * la fenêtre de configuration d'un carré, ou en les copiant directement dans ce dossier
 * depuis un PC en USB.
 */
object SoundLibrary {

    private const val AUDIO_SUBDIR = "sounds"

    fun soundsDir(context: Context): File {
        val dir = context.getExternalFilesDir(AUDIO_SUBDIR) ?: File(context.filesDir, AUDIO_SUBDIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun listSounds(context: Context): List<String> {
        return soundsDir(context)
            .listFiles { file -> file.isFile && isAudioFile(file.name) }
            ?.map { it.name }
            ?.sorted()
            ?: emptyList()
    }

    fun soundFile(context: Context, name: String): File = File(soundsDir(context), name)

    /** Copie le fichier pointé par [sourceUri] (choisi via le sélecteur système) dans la
     * bibliothèque locale de l'appli, et retourne le nom de fichier stocké (à utiliser
     * ensuite comme référence de son pour un carré), ou null en cas d'échec. */
    fun importFrom(context: Context, sourceUri: Uri): String? {
        val displayName = queryDisplayName(context, sourceUri) ?: return null
        if (!isAudioFile(displayName)) return null

        return try {
            val destFile = uniqueDestination(soundsDir(context), sanitizeFileName(displayName))
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                destFile.outputStream().use { output -> input.copyTo(output) }
            } ?: return null
            destFile.name
        } catch (e: Exception) {
            null
        }
    }

    /** Copie tous les fichiers audio trouvés dans le dossier pointé par [treeUri] (choisi
     * via le sélecteur de dossier système), y compris dans ses sous-dossiers (ex :
     * "sons/Départ/aabdepart1.wav"), dans la bibliothèque locale de l'appli. Les fichiers
     * sont retrouvés par leur seul nom, peu importe le sous-dossier où ils se trouvent,
     * et un fichier existant du même nom est écrasé. Retourne le nombre de fichiers
     * effectivement copiés. */
    fun importFolder(context: Context, treeUri: Uri): Int {
        val root = DocumentFile.fromTreeUri(context, treeUri) ?: return 0
        var count = 0

        fun walk(folder: DocumentFile) {
            for (doc in folder.listFiles()) {
                if (doc.isDirectory) {
                    walk(doc)
                    continue
                }

                val name = doc.name ?: continue
                if (!isAudioFile(name)) continue

                val destFile = File(soundsDir(context), sanitizeFileName(name))
                val copied = runCatching {
                    context.contentResolver.openInputStream(doc.uri)?.use { input ->
                        destFile.outputStream().use { output -> input.copyTo(output) }
                    } != null
                }.getOrDefault(false)

                if (copied) count++
            }
        }

        walk(root)
        return count
    }

    private fun isAudioFile(name: String): Boolean =
        name.endsWith(".mp3", ignoreCase = true) || name.endsWith(".wav", ignoreCase = true)

    private fun queryDisplayName(context: Context, uri: Uri): String? {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0 && cursor.moveToFirst()) {
                    return cursor.getString(index)
                }
            }
        return null
    }

    private fun sanitizeFileName(name: String): String {
        val cleaned = name.replace(Regex("[^A-Za-z0-9._ '\\-]"), "_")
        return cleaned.ifBlank { "son_${System.currentTimeMillis()}.mp3" }
    }

    private fun uniqueDestination(dir: File, name: String): File {
        var candidate = File(dir, name)
        if (!candidate.exists()) return candidate

        val dotIndex = name.lastIndexOf('.')
        val base = if (dotIndex >= 0) name.substring(0, dotIndex) else name
        val extension = if (dotIndex >= 0) name.substring(dotIndex) else ""

        var i = 2
        while (candidate.exists()) {
            candidate = File(dir, "$base ($i)$extension")
            i++
        }
        return candidate
    }
}
