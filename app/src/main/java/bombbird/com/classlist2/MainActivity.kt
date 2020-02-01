package bombbird.com.classlist2

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    companion object MainActivity {
        const val RC_UPDATE_LIST = 7779
    }

    private var name: String = ""
    private var className: String = ""
    private val students = ArrayList<String>()
    private val studentsBool = HashMap<String, Boolean>()
    private var listLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab_openLists.setOnClickListener {
            val intent = Intent(this, OpenListActivity::class.java)
            startActivityForResult(intent, RC_UPDATE_LIST)
        }

        fab_openClasses.setOnClickListener {
            val intent = Intent(this, OpenClassActivity::class.java)
            startActivity(intent)
        }

        FileHandler.initDirs(this)
        loadButtons()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_UPDATE_LIST) {
                updateLists(data?.getStringExtra("listName"))
            }
        }
    }

    private fun updateLists(newName: String?) {
        listLoading = true
        students.clear()
        studentsBool.clear()
        clearCheckboxes()
        if (newName != null) {
            name = newName
        } else {
            return
        }
        if (name.isEmpty()) return
        noListTextView.visibility = View.INVISIBLE
        fab_deleteList.show()
        val file = FileHandler.loadFile("lists/$name", this)
        var index = 0
        var prevCheckBox = CheckBox(this)
        file.forEachLine { content ->
            if (content.isNotEmpty()) {
                when {
                    index == 0 -> {
                        className = content
                        nameTextView.text = resources.getString(R.string.class_list_name, content, name)
                        nameTextView.visibility = View.VISIBLE
                    }
                    index % 2 == 1 -> {
                        //Add name
                        prevCheckBox = CheckBox(this)
                        prevCheckBox.layoutParams = checkBox1.layoutParams
                        prevCheckBox.text = content
                        prevCheckBox.setOnCheckedChangeListener { button, checked ->
                            if (listLoading) return@setOnCheckedChangeListener
                            studentsBool[button.text.toString()] = checked
                            saveList()
                        }
                        linearLayout.addView(prevCheckBox)
                        students.add(content)
                    }
                    else -> {
                        //Set checked
                        val checked = content == "1"
                        prevCheckBox.isChecked = checked
                        studentsBool[students.last()] = checked
                    }
                }
                index++
            }
        }
        listLoading = false
    }

    private fun saveList() {
        val stringBuilder = StringBuilder()
        stringBuilder.append("$className\n")
        for (student: String in students) {
            stringBuilder.append("$student\n")
            stringBuilder.append((if (studentsBool.containsKey(student) && studentsBool[student]!!) "1" else "0") + "\n")
        }
        FileHandler.saveListFile(name, stringBuilder.toString(), this)
    }

    private fun loadButtons() {
        fab_deleteList.setOnClickListener {
            if (name.isNotEmpty()) {
                val builder: AlertDialog.Builder = let {
                    AlertDialog.Builder(it)
                }
                builder.setPositiveButton(R.string.dialog_delete) { _, _ ->
                    FileHandler.deleteListFile(name, this)
                    clearCheckboxes()
                }
                builder.setNegativeButton(R.string.dialog_cancel) { _, _ -> }
                builder.setMessage("Delete $name?")
                builder.show()
            }
        }
    }

    private fun clearCheckboxes() {
        val toRemove = ArrayList<View>()
        for (i in 1..linearLayout.childCount) {
            val view = linearLayout.getChildAt(i)
            if (view is CheckBox && view.tag != "checkBox1") {
                toRemove.add(view)
            }
        }
        for (view: View in toRemove) {
            linearLayout.removeView(view)
        }
        noListTextView.visibility = View.VISIBLE
        fab_deleteList.hide()
        className = ""
        nameTextView.text = ""
        nameTextView.visibility = View.INVISIBLE
    }
}
