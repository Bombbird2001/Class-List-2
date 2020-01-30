package bombbird.com.classlist2

import android.content.Context
import java.io.File

class FileHandler {
    companion object FileHandler {
        fun loadFile(name: String, context: Context): File {
            val path = context.filesDir
            return File(path, name)
        }

        fun saveFile(name: String, content: String, context: Context): Boolean {
            val path = context.filesDir
            val file = File(path, name)
            if (file.canWrite()) {
                file.writeText(content)
                return true
            }
            return false
        }
    }
}