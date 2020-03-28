package bombbird.com.classlist2

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.checkbox_recycler.view.*

class CheckboxRecyclerAdapter(private var students: ArrayList<String>, private var checked: HashMap<String, Boolean>, private var comments: HashMap<String, String>, private val activity: MainActivity):
    RecyclerView.Adapter<CheckboxRecyclerAdapter.CheckboxRecyclerViewHolder>() {

    lateinit var recyclerView: RecyclerView
    private var loading = false

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

        val checkBox = holder.itemView.checkBox2
        val editComment = holder.itemView.comment_input
        val commentView = holder.itemView.comment_textView
        val editButton = holder.itemView.edit_comment_button
        val confirmButton = holder.itemView.save_comment_button

        loading = true
        checkBox.text = name
        checkBox.isChecked = if (checked.containsKey(name)) checked[name]!! else false
        editComment.editText?.setText(if (comments.containsKey(name)) comments[name] else "")
        commentView.text = if (comments.containsKey(name)) comments[name] else ""
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, activity.fontSize)
        editComment.editText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, activity.fontSize)
        commentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, activity.fontSize)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (loading) return@setOnCheckedChangeListener
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

            editComment.editText?.setText(comments[name])
            editComment.editText?.requestFocus()
            (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(editComment.editText, InputMethodManager.SHOW_IMPLICIT)
            editComment.editText?.length()?.let { it1 -> editComment.editText?.setSelection(it1) }
        }

        confirmButton.setOnClickListener {
            commentView.visibility = View.VISIBLE
            if (editComment.editText != null && editComment.editText?.isFocused!!) (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(editComment.editText?.windowToken, 0)
            editComment.visibility = View.INVISIBLE
            it.visibility = View.INVISIBLE
            editButton.visibility = View.VISIBLE

            commentView.text = editComment.editText?.text.toString()

            comments[name] = commentView.text.toString()

            activity.updateData(checked, comments)
        }
        loading = false
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