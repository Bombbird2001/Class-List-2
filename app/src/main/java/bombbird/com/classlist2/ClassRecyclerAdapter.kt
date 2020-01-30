package bombbird.com.classlist2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class ClassRecyclerAdapter(private val classes: ArrayList<String>, private val activity: OpenClassActivity):
    RecyclerView.Adapter<ClassRecyclerAdapter.ClassRecyclerViewHolder>() {

    lateinit var recyclerView: RecyclerView

    class ClassRecyclerViewHolder(view: View):
        RecyclerView.ViewHolder(view)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ClassRecyclerViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.class_recycler, parent, false) as View
        // set the view's size, margins, paddings and layout parameters
        return ClassRecyclerViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ClassRecyclerViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val classButton = holder.itemView.findViewById<Button>(R.id.classButton)
        classButton.text = classes[holder.adapterPosition]

        classButton.setOnClickListener {
            //Editclassactivity with class name
            val intent = Intent(activity, EditClassActivity::class.java)
            intent.putExtra("className", classButton.text.toString())
            activity.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = classes.size
}