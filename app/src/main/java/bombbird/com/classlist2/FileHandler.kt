package bombbird.com.classlist2

import android.content.Context
import android.widget.Toast
import java.io.File

class FileHandler {
    companion object FileHandler {
        fun listFilesInBaseDirectory(context: Context): Array<File> {
            return context.filesDir.listFiles()
        }

        fun loadFile(name: String, context: Context): File {
            val path = context.filesDir
            return File(path, name)
        }

        fun saveFile(name: String, content: String, context: Context): Boolean {
            val path = context.filesDir
            val file = File(path, name)
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
    }
}