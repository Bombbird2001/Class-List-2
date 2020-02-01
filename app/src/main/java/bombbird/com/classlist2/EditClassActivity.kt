package bombbird.com.classlist2

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.NumberPicker
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
    private var otherClasses: ArrayList<String> = ArrayList()

    private var classOk: Boolean = false
    private var arrayOk: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_class)

        loadStudents()
        loadRecycler()

        if (intent.hasExtra("otherClasses")) {
            otherClasses = intent.getStringArrayListExtra("otherClasses")
        }

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
                    if (otherClasses.contains(s.toString())) {
                        classOk = false
                        classInputLayout.error = resources.getString(R.string.name_exists)
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
        arrayOk = checkStudents()
        updateFabVisibility()
    }

    @SuppressLint("RestrictedApi")
    private fun updateFabVisibility() {
        if (classOk && arrayOk) {
            fab_confirmAddClass.show()
        } else {
            fab_confirmAddClass.hide()
        }
    }

    private fun initFabCheck() {
        arrayOk = checkStudents()
        if (classInput.text?.isNotEmpty()!! && classInput.text?.length!! < 30) {
            classOk = true
        }
        updateFabVisibility()
    }

    private fun checkStudents(): Boolean {
        for (s in students) {
            if (s.isNotEmpty()) {
                return true
            }
        }
        return false
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

    @SuppressLint("RestrictedApi")
    private fun loadButtons() {
        button2.setOnClickListener {
            val builder: AlertDialog.Builder = let {
                AlertDialog.Builder(it)
            }
            builder.setTitle(R.string.dialog_resize)
            builder.setMessage("\nChange class size to:")
            val dialogView = layoutInflater.inflate(R.layout.dialog_number_picker, findViewById(R.id.constraintLayout1), false)
            builder.setView(dialogView)
            val np = dialogView.findViewById<NumberPicker>(R.id.numberPicker1)
            np.minValue = 1
            np.maxValue = 100
            np.value = students.size
            np.wrapSelectorWheel = false
            np.setOnValueChangedListener { _, _, _ -> }
            builder.setPositiveButton(R.string.dialog_set) { _, _ ->
                (viewAdapter as StudentRecyclerAdapter).resizeTo(np.value)
            }
            builder.setNegativeButton(R.string.dialog_cancel) { _, _ -> }
            builder.show()
        }

        fab_confirmAddClass.setOnClickListener {
            if (intent.hasExtra("className") && classInputLayout.classInput.text.toString() != intent.getStringExtra("className")) {
                val newName = classInputLayout.classInput.text.toString()
                val oldName = intent.getStringExtra("className")
                val builder: AlertDialog.Builder = let {
                    AlertDialog.Builder(it)
                }
                builder.setPositiveButton(R.string.dialog_delete) { _, _ ->
                    FileHandler.deleteFile(oldName, this)
                    saveClass()
                }
                builder.setNegativeButton(R.string.dialog_keep) { _, _ ->
                    saveClass()
                }
                builder.setNeutralButton(R.string.dialog_cancel) { _, _ -> }
                builder.setMessage("Class has been renamed from $oldName to $newName, delete old class?")
                builder.show()
            } else {
                saveClass()
            }
        }

        if (intent.hasExtra("className")) {
            fab_deleteClass.setOnClickListener {
                val builder: AlertDialog.Builder = let {
                    AlertDialog.Builder(it)
                }
                builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
                    FileHandler.deleteFile(intent.getStringExtra("className"), this)
                    finish()
                }
                builder.setNegativeButton(R.string.dialog_cancel) { _, _ -> }
                builder.setMessage("Delete class?")
                builder.show()
            }
            fab_deleteClass.visibility = View.VISIBLE
        }
    }

    private fun saveClass() {
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
