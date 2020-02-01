package bombbird.com.classlist2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    companion object MainActivity {
        const val RC_UPDATE_LIST = 7779
    }

    private val students = ArrayList<String>()
    private val studentsBool = ArrayList<Boolean>()

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

        for (i in 1..5) {
            //TODO Add list of lists later
            val checkbox = CheckBox(this)
            checkbox.layoutParams = checkBox1.layoutParams
            checkbox.setText(R.string.loading_name)
            linearLayout.addView(checkbox)

        }

        FileHandler.initDirs(this)
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

    private fun updateLists(name: String?) {
        for (i in 1..linearLayout.childCount) {
            val view = linearLayout.getChildAt(i)
            if (view is CheckBox && view.tag != "checkBox1") linearLayout.removeView(view)
        }
        if (name.isNullOrEmpty()) return
        val file = FileHandler.loadFile("lists/$name", this)
        var index = 0
        var prevCheckBox = CheckBox(this)
        file.forEachLine { content ->
            if (content.isNotEmpty()) {
                if (index % 2 == 0) {
                    //Add name
                    prevCheckBox = CheckBox(this)
                    prevCheckBox.layoutParams = checkBox1.layoutParams
                    prevCheckBox.text = content
                    linearLayout.addView(prevCheckBox)
                } else {
                    //Set checked
                    prevCheckBox.isChecked = content == "1"
                }
                index++
            }
        }
    }
}
