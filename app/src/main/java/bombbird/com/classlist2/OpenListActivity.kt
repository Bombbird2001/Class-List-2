package bombbird.com.classlist2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_open_list.*

class OpenListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val lists: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_list)

        fab_newList.setOnClickListener {
            val intent = Intent(this, NewListActivity::class.java)
            intent.putStringArrayListExtra("otherLists", lists)
            startActivity(intent)
        }

        loadLists()
        displayLists()
        loadRecycler()
    }

    override fun onResume() {
        super.onResume()

        loadLists()
        (viewAdapter as ClassRecyclerAdapter).updateData(lists)
        displayLists()
    }

    private fun loadLists() {
        lists.clear()
        for (file in FileHandler.listFilesInDirectory("lists", this)) {
            lists.add(file.name)
        }
    }

    private fun displayLists() {
        if (lists.isEmpty()) {
            textView3.visibility = View.INVISIBLE
            textView4.visibility = View.VISIBLE
        } else {
            textView3.visibility = View.VISIBLE
            textView4.visibility = View.INVISIBLE
        }
    }

    private fun loadRecycler() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = ClassRecyclerAdapter(lists, this)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView3).apply {
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        (viewAdapter as ClassRecyclerAdapter).recyclerView = recyclerView
    }
}
