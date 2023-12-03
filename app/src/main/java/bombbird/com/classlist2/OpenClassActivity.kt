package bombbird.com.classlist2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bombbird.com.classlist2.databinding.ActivityOpenClassBinding

class OpenClassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenClassBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val classes: ArrayList<String> = ArrayList()

    private lateinit var textView: TextView
    private lateinit var textView2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenClassBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        textView = binding.textView
        textView2 = binding.textView2

        binding.fabNewClass.setOnClickListener {
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
        (viewAdapter as ClassRecyclerAdapter).updateData(classes)
        displayClasses()
    }

    private fun loadClasses() {
        classes.clear()
        for (file in FileHandler.listFilesInDirectory("classes", this)) {
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

        recyclerView = binding.recyclerView2.apply {
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        (viewAdapter as ClassRecyclerAdapter).recyclerView = recyclerView
    }
}
