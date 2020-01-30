package bombbird.com.classlist2

import android.content.Context
import android.widget.Toast

class ToastManager {
    companion object ToastManager {
        fun toastSaveFail(context: Context) {
            val toast = Toast.makeText(context, context.resources.getString(R.string.file_save_fail), Toast.LENGTH_LONG)
            toast.show()
        }

        fun toastLoadFail(context: Context) {
            val toast = Toast.makeText(context, context.resources.getString(R.string.file_load_fail), Toast.LENGTH_LONG)
            toast.show()
        }
    }
}