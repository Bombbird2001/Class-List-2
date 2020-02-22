package bombbird.com.classlist2

import android.content.Context
import org.json.JSONObject
import java.io.File

class FileHandler {
    companion object FileHandler {
        fun initDirs(context: Context) {
            val classDir = File(context.filesDir, "classes")
            if (!classDir.exists()) classDir.mkdir()
            val listDir = File(context.filesDir, "lists")
            if (!listDir.exists()) listDir.mkdir()
        }

        fun listFilesInDirectory(dir: String, context: Context): Array<File> {
            return File(context.filesDir, dir).listFiles()
        }

        fun loadFile(name: String, context: Context): File {
            val path = context.filesDir
            return File(path, name)
        }

        fun saveClassFile(name: String, content: String, context: Context): Boolean {
            val path = File(context.filesDir, "classes")
            return saveFile(File(path, name), content, context)
        }

        fun saveListFile(name: String, content: String, context: Context): Boolean {
            val path = File(context.filesDir, "lists")
            return saveFile(File(path, name), content, context)
        }

        fun deleteClassFile(name: String, context: Context): Boolean {
            val path = File(context.filesDir, "classes")
            return deleteFile(File(path, name))
        }

        fun deleteListFile(name: String, context: Context): Boolean {
            val path = File(context.filesDir, "lists")
            return deleteFile(File(path, name))
        }

        private fun saveFile(file: File, content: String, context: Context): Boolean {
            if (!file.exists() && !file.createNewFile()) {
                ToastManager.toastSaveFail(context)
                return false
            }
            if (file.canWrite()) {
                file.writeText(content)
                return true
            }
            ToastManager.toastSaveFail(context)
            return false
        }

        private fun deleteFile(file: File): Boolean {
            if (!file.exists()) return false
            return file.delete()
        }

        fun loadJson(name: String, context: Context): JSONObject {
            return JSONObject(loadFile(name, context).readText())
        }
    }
}