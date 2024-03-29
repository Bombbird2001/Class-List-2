package bombbird.com.classlist2

import android.app.AlertDialog
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import bombbird.com.classlist2.databinding.CheckboxRecyclerBinding

class CheckboxRecyclerAdapter(private var students: ArrayList<String>,
                              private var checked: HashMap<String, Boolean>,
                              private var comments: HashMap<String, String>,
                              private val activity: MainActivity):
    RecyclerView.Adapter<CheckboxRecyclerAdapter.CheckboxRecyclerViewHolder>() {

    lateinit var recyclerView: RecyclerView
    private var loading = false

    class CheckboxRecyclerViewHolder(val binding: CheckboxRecyclerBinding):
        RecyclerView.ViewHolder(binding.root)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CheckboxRecyclerViewHolder {
        return CheckboxRecyclerViewHolder(CheckboxRecyclerBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CheckboxRecyclerViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val name = students[holder.adapterPosition]
        val checkBox = holder.binding.checkBox2
        val editComment = holder.binding.commentInput
        val commentView = holder.binding.commentTextView
        val editButton = holder.binding.editCommentButton
        val confirmButton = holder.binding.saveCommentButton

        loading = true
        checkBox.text = name
        checkBox.isChecked = if (checked.containsKey(name)) checked[name] == true else false
        editComment.editText?.setText(if (comments.containsKey(name)) comments[name] else "")
        commentView.text = if (comments.containsKey(name)) comments[name] else ""
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, activity.fontSize)
        editComment.editText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, activity.fontSize)
        commentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, activity.fontSize)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (loading) return@setOnCheckedChangeListener
            if (activity.confirmCheckbox) {
                val builder: AlertDialog.Builder = let {
                    AlertDialog.Builder(activity)
                }
                builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
                    checked[name] = isChecked
                    activity.updateData(checked, comments)
                }
                builder.setNegativeButton(R.string.dialog_cancel) { _, _ ->
                    loading = true
                    checkBox.isChecked = !isChecked
                    loading = false
                }
                builder.setMessage("${if (isChecked) "Check" else "Uncheck"} $name?")
                builder.setCancelable(false)
                builder.show()
            } else {
                checked[name] = isChecked
                activity.updateData(checked, comments)
            }
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