package bombbird.com.classlist2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_edit_class.*
import kotlinx.android.synthetic.main.activity_edit_class.view.*
import java.lang.StringBuilder

class EditClassActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var students: ArrayList<String> = ArrayList()

    private var classOk: Boolean = false
    private var arrayOk: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_class)

        loadStudents()
        loadRecycler()

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
                            classOk = false
                            classInputLayout.error = null
                        }
                        s.length >= 30 -> {
                            classOk = false
                            classInputLayout.error = resources.getString(R.string.text_too_long)
                        }
                        else -> {
                            classOk = true
                            classInputLayout.error = null
                        }
                    }
                    updateFabVisibility()
                }
            }
        })

        loadButtons()
        initFabCheck()
    }

    fun updateArray(students: ArrayList<String>) {
        this.students = students
        arrayOk = false
        for (s in students) {
            if (s.isNotEmpty()) {
                arrayOk = true
                break
            }
        }
        updateFabVisibility()
    }

    private fun updateFabVisibility() {
        if (classOk && arrayOk) {
            fab_confirmAddClass.show()
        } else {
            fab_confirmAddClass.hide()
        }
    }

    private fun initFabCheck() {
        if (students.size > 0) {
            arrayOk = true
        }
        if (classInput.text?.isNotEmpty()!! && classInput.text?.length!! < 30) {
            classOk = true
        }
        updateFabVisibility()
    }

    private fun loadStudents() {
        students.add("")
        if (intent.hasExtra("className")) {
            val className = intent.getStringExtra("className")
            classInput.setText(className)
            val file = FileHandler.loadFile(className, this)
            if (!file.exists()) {
                ToastManager.toastLoadFail(this)
                return
            }
            students.clear()
            file.forEachLine { student ->
                if (student.isNotEmpty()) {
                    students.add(student)
                }
            }
        }
    }

    private fun loadRecycler() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = StudentRecyclerAdapter(students, this)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        (viewAdapter as StudentRecyclerAdapter).recyclerView = recyclerView
    }

    private fun loadButtons() {
        button2.setOnClickListener {
            (viewAdapter as StudentRecyclerAdapter).resizeTo(button2.text.toString().toInt())
        }

        button3.setOnClickListener {
            (viewAdapter as StudentRecyclerAdapter).resizeTo(button3.text.toString().toInt())
        }

        button4.setOnClickListener {
            (viewAdapter as StudentRecyclerAdapter).resizeTo(button4.text.toString().toInt())
        }

        fab_confirmAddClass.setOnClickListener {
            val sb = StringBuilder()
            for (student: String in students) {
                if (sb.isNotEmpty()) {
                    sb.append("\n")
                }
                if ("" != student) {
                    sb.append(student)
                }
            }
            FileHandler.saveFile(classInputLayout.classInput.text.toString(), sb.toString(), this)
            finish()
        }
    }
}
