package bombbird.com.classlist2

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bombbird.com.classlist2.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    companion object MainActivity {
        const val RC_UPDATE_LIST = 7779
    }

    private var name: String = ""
    private var className: String = ""
    private val students = ArrayList<String>()
    private var studentsBool = HashMap<String, Boolean>()
    private var comments = HashMap<String, String>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: CheckboxRecyclerAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityMainBinding

    private lateinit var fabOpenLists: FloatingActionButton
    private lateinit var fabOpenClasses: FloatingActionButton
    private lateinit var noListTextView: TextView
    private lateinit var fabDeleteList: FloatingActionButton
    private lateinit var nameTextView: TextView

    var fontSize = 14f
    var confirmCheckbox = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        fabOpenLists = binding.contentMain.fabOpenLists
        fabOpenClasses = binding.contentMain.fabOpenClasses
        noListTextView = binding.contentMain.noListTextView
        fabDeleteList = binding.contentMain.fabDeleteList
        nameTextView = binding.contentMain.nameTextView

        fabOpenLists.setOnClickListener {
            val intent = Intent(this, OpenListActivity::class.java)
            startActivityForResult(intent, RC_UPDATE_LIST)
        }

        fabOpenClasses.setOnClickListener {
            val intent = Intent(this, OpenClassActivity::class.java)
            startActivity(intent)
        }

        FileHandler.initDirs(this)
        loadButtons()

        val temp = PreferenceManager.getDefaultSharedPreferences(this).getString("font_size", "14")?.toFloatOrNull()
        if (temp != null) fontSize = temp

        val theme = PreferenceManager.getDefaultSharedPreferences(this).getString("display_theme", "-1")?.toIntOrNull()
        if (theme != null) AppCompatDelegate.setDefaultNightMode(theme)

        loadRecycler()

        confirmCheckbox = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("confirm_checkbox", false)

        noListTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
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
        fabDeleteList.show()
        try {
            val data = FileHandler.loadJson("lists/$name", this)
            className = data.getString("className")
            nameTextView.text = resources.getString(R.string.class_list_name, className, name)
            nameTextView.visibility = View.VISIBLE
            val studentArray = data.getJSONArray("students")
            for (i in 0 until studentArray.length()) {
                val obj = studentArray.getJSONObject(i)
                val name = obj.getString("name")
                students.add(name)
                studentsBool[name] = obj.getBoolean("checked")
                comments[name] = obj.getString("comments")
            }
        } catch (e: JSONException) {
            Log.w("JSON Parse", "Failed to parse file into JSON, using old parse method")
            val file = FileHandler.loadFile("lists/$name", this)
            var index = 0
            file.forEachLine { content ->
                if (content.isNotEmpty()) {
                    when {
                        index == 0 -> {
                            className = content
                            nameTextView.text = resources.getString(R.string.class_list_name, content, name)
                            nameTextView.visibility = View.VISIBLE
                        }
                        index % 2 == 1 -> {
                            students.add(content)
                        }
                        else -> {
                            studentsBool[students.last()] = content == "1"
                        }
                    }
                    index++
                }
            }
        }
        viewAdapter.updateData(students, studentsBool, comments)
    }

    private fun saveList() {
        val data = JSONObject()
        data.put("className", className)
        val studentArray = JSONArray()
        for (student: String in students) {
            val studentObj = JSONObject()
            studentObj.put("name", student)
            studentObj.put("checked", studentsBool.containsKey(student) && studentsBool[student]!!)
            studentObj.put("comments", if (comments.containsKey(student)) comments[student] else "")
            studentArray.put(studentObj)
        }
        data.put("students", studentArray)

        FileHandler.saveListFile(name, data.toString(4), this)
    }

    fun updateData(checked: HashMap<String, Boolean>, comments: HashMap<String, String>) {
        studentsBool = checked
        this.comments = comments
        saveList()
    }

    private fun loadButtons() {
        fabDeleteList.setOnClickListener {
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
        students.clear()
        studentsBool.clear()
        comments.clear()

        viewAdapter.updateData(students, studentsBool, comments)

        noListTextView.visibility = View.VISIBLE
        fabDeleteList.hide()
        className = ""
        nameTextView.text = ""
        nameTextView.visibility = View.INVISIBLE
    }

    private fun loadRecycler() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = CheckboxRecyclerAdapter(students, studentsBool, comments, this)

        recyclerView = binding.contentMain.recyclerView4.apply {
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        viewAdapter.recyclerView = recyclerView
    }
}
