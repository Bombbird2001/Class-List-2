package bombbird.com.classlist2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_new_class.*

class NewClassActivity : AppCompatActivity() {

    private val students: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_class)

        classInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            @SuppressLint("RestrictedApi")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    when {
                        s.isEmpty() -> {
                            fab_confirmAddClass.visibility = View.INVISIBLE
                            textView3.visibility = View.INVISIBLE
                        }
                        s.length > 30 -> {
                            fab_confirmAddClass.visibility = View.INVISIBLE
                            textView3.visibility = View.VISIBLE
                        }
                        else -> {
                            fab_confirmAddClass.visibility = View.VISIBLE
                            textView3.visibility = View.INVISIBLE
                        }
                    }
                    if (students.isEmpty()) fab_confirmAddClass.visibility = View.INVISIBLE
                }
            }
        })

        nameInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length > 30) {
                    textView5.visibility = View.VISIBLE
                } else {
                    textView5.visibility = View.INVISIBLE
                }
            }
        })

        button2.setOnClickListener {
            if (nameInput.text.isEmpty()) return@setOnClickListener

        }
    }
}
