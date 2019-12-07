package bombbird.com.classlist2

import android.content.Context
import java.io.File

class FileHandler {
    companion object FileHandler {
        fun loadFile(name: String, context: Context): File {
            val path = context.filesDir
            return File(path, name)
        }
    }
}