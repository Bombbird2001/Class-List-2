package bombbird.com.classlist2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_open_class.*

class OpenClassActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val classes: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_class)

        fab_newClass.setOnClickListener {
            val intent = Intent(this, EditClassActivity::class.java)
            startActivity(intent)
        }

        loadClasses()
        displayClasses()
        loadRecycler()
    }

    override fun onResume() {
        super.onResume()
        loadClasses()
        (viewAdapter as ClassRecyclerAdapter).updateClasses(classes)
        displayClasses()
    }

    private fun loadClasses() {
        classes.clear()
        for (file in FileHandler.listFilesInBaseDirectory(this)) {
            classes.add(file.name)
        }
    }

    private fun displayClasses() {
        if (classes.isEmpty()) {
            textView.visibility = View.INVISIBLE
            textView2.visibility = View.VISIBLE
        } else {
            textView.visibility = View.VISIBLE
            textView2.visibility = View.INVISIBLE
        }
    }

    private fun loadRecycler() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = ClassRecyclerAdapter(classes, this)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView2).apply {
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        (viewAdapter as ClassRecyclerAdapter).recyclerView = recyclerView
    }
}
