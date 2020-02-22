package bombbird.com.classlist2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CheckboxRecyclerAdapter(private var students: ArrayList<String>, private var checked: HashMap<String, Boolean>, private var comments: HashMap<String, String>, private val activity: MainActivity):
    RecyclerView.Adapter<CheckboxRecyclerAdapter.CheckboxRecyclerViewHolder>() {

    lateinit var recyclerView: RecyclerView

    class CheckboxRecyclerViewHolder(view: View):
        RecyclerView.ViewHolder(view)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CheckboxRecyclerViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.checkbox_recycler, parent, false) as View
        // set the view's size, margins, paddings and layout parameters

        return CheckboxRecyclerViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CheckboxRecyclerViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val name = students[holder.adapterPosition]

        val checkBox = holder.itemView.findViewById<CheckBox>(R.id.checkBox2)
        val editComment = holder.itemView.findViewById<EditText>(R.id.comment_editText)
        val commentView = holder.itemView.findViewById<TextView>(R.id.comment_textView)
        val editButton = holder.itemView.findViewById<Button>(R.id.edit_comment_button)
        val confirmButton = holder.itemView.findViewById<Button>( R.id.save_comment_button)

        checkBox.text = name
        checkBox.isChecked = if (checked.containsKey(name)) checked[name]!! else false
        editComment.setText(if (comments.containsKey(name)) comments[name] else "")
        commentView.text = if (comments.containsKey(name)) comments[name] else ""

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            checked[name] = isChecked
            activity.updateData(checked, comments)
        }

        editComment.visibility = View.INVISIBLE
        commentView.visibility = View.VISIBLE
        editButton.visibility = View.VISIBLE
        confirmButton.visibility = View.INVISIBLE

        editButton.setOnClickListener {
            commentView.visibility = View.INVISIBLE
            editComment.visibility = View.VISIBLE
            it.visibility = View.INVISIBLE
            confirmButton.visibility = View.VISIBLE

            editComment.setText(comments[name])
        }

        confirmButton.setOnClickListener {
            commentView.visibility = View.VISIBLE
            editComment.visibility = View.INVISIBLE
            it.visibility = View.INVISIBLE
            editButton.visibility = View.VISIBLE

            commentView.text = editComment.text.toString()

            comments[name] = commentView.text.toString()

            activity.updateData(checked, comments)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = students.size

    fun updateData(students: ArrayList<String>, checked: HashMap<String, Boolean>, comments: HashMap<String, String>) {
        this.students = students
        this.checked = checked
        this.comments = comments
        notifyDataSetChanged()
    }
}