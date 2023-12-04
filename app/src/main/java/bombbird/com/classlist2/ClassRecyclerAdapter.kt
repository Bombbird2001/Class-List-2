package bombbird.com.classlist2

import android.app.Activity
import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import bombbird.com.classlist2.databinding.ClassListRecyclerBinding

class ClassRecyclerAdapter(private var classes: ArrayList<String>,
                           private val activity: Activity):
    RecyclerView.Adapter<ClassRecyclerAdapter.ClassRecyclerViewHolder>() {

    lateinit var recyclerView: RecyclerView

    class ClassRecyclerViewHolder(val binding: ClassListRecyclerBinding):
        RecyclerView.ViewHolder(binding.root)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ClassRecyclerViewHolder {
        return ClassRecyclerViewHolder(ClassListRecyclerBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ClassRecyclerViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val classButton = holder.binding.classOrListButton
        classButton.text = classes[holder.adapterPosition]
        PreferenceManager.getDefaultSharedPreferences(activity)
            .getString("font_size", "14")?.toFloatOrNull()?.let {
            classButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, it)
        }

        classButton.setOnClickListener {
            if (activity is OpenClassActivity) {
                //EditClassActivity with class name
                val intent = Intent(activity, EditClassActivity::class.java)
                intent.putExtra("className", classButton.text.toString())
                val newClasses = classes
                classes.remove(classButton.text.toString())
                intent.putStringArrayListExtra("otherClasses", newClasses)
                activity.startActivity(intent)
            } else if (activity is OpenListActivity) {
                //Back to MainActivity with list name
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
