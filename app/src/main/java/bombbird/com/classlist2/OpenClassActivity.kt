package bombbird.com.classlist2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_open_class.*

class OpenClassActivity : AppCompatActivity() {
    private val classes: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_class)

        fab_newClass.setOnClickListener {
            val intent = Intent(this, NewClassActivity::class.java)
            startActivity(intent)
        }

        loadClasses()
        displayClasses()
    }

    private fun loadClasses() {
        val file = FileHandler.loadFile("classes.txt", this)
        println(file.absolutePath + " " + file.exists())
        if (!file.exists()) {
            if (file.createNewFile()) println("File created")
            loadClasses()
            return
        }
        file.forEachLine {clas ->
            classes.add(clas)
        }
        println(classes.size)
    }

    private fun displayClasses() {
        if (classes.isEmpty()) {
            textView.visibility = View.INVISIBLE
            textView2.visibility = View.VISIBLE
        } else {
            textView.visibility = View.VISIBLE
            textView2.visibility = View.INVISIBLE
        }

        for (name in classes) {
            val button1 = Button(this)
            button1.text = name
            button1.layoutParams = button.layoutParams
            linearLayout1.addView(button1)
        }
    }
}
