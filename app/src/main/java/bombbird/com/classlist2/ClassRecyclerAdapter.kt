package bombbird.com.classlist2

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class ClassRecyclerAdapter(private var classes: ArrayList<String>, private val activity: Activity):
    RecyclerView.Adapter<ClassRecyclerAdapter.ClassRecyclerViewHolder>() {

    lateinit var recyclerView: RecyclerView

    class ClassRecyclerViewHolder(view: View):
        RecyclerView.ViewHolder(view)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ClassRecyclerViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.class_list_recycler, parent, false) as View
        // set the view's size, margins, paddings and layout parameters
        return ClassRecyclerViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ClassRecyclerViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val classButton = holder.itemView.findViewById<Button>(R.id.classOrListButton)
        classButton.text = classes[holder.adapterPosition]

        classButton.setOnClickListener {
            if (activity is OpenClassActivity) {
                //Editclassactivity with class name
                val intent = Intent(activity, EditClassActivity::class.java)
                intent.putExtra("className", classButton.text.toString())
                val newClasses = classes
                classes.remove(classButton.text.toString())
                intent.putStringArrayListExtra("otherClasses", newClasses)
                activity.startActivity(intent)
            } else if (activity is OpenListActivity) {
                //Back to mainactivity with list name
                val intent = Intent()
                intent.putExtra("listName", classButton.text.toString())
                activity.setResult(Activity.RESULT_OK, intent)
                activity.finish()
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = classes.size

    fun updateData(classes: ArrayList<String>) {
        this.classes = classes
        notifyDataSetChanged()
    }
}