package bombbird.com.classlist2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_edit_class.*

class EditClassActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var students: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_class)

        students.add("")

        viewManager = LinearLayoutManager(this)
        viewAdapter = StudentRecyclerAdapter(students)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        (viewAdapter as StudentRecyclerAdapter).recyclerView = recyclerView

        classInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("RestrictedApi")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    when {
                        s.isEmpty() -> {
                            fab_confirmAddClass.hide()
                            classInputLayout.error = null
                        }
                        s.length > 30 -> {
                            fab_confirmAddClass.hide()
                            classInputLayout.error = resources.getString(R.string.text_too_long)
                        }
                        else -> {
                            fab_confirmAddClass.show()
                            classInputLayout.error = null
                        }
                    }
                    if (students.isEmpty()) fab_confirmAddClass.visibility = View.INVISIBLE
                }
            }
        })
    }
}
