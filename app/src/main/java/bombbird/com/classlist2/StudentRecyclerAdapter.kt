package bombbird.com.classlist2

import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout

class StudentRecyclerAdapter(val students: ArrayList<String>, val activity: EditClassActivity):
        RecyclerView.Adapter<StudentRecyclerAdapter.StudentRecyclerViewHolder>() {

    lateinit var recyclerView: RecyclerView
    var prevSize = 0

    class StudentRecyclerViewHolder(view: View):
        RecyclerView.ViewHolder(view)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): StudentRecyclerViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.students_recycler, parent, false) as View
        // set the view's size, margins, paddings and layout parameters
        return StudentRecyclerViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: StudentRecyclerViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val studentInputLayout = holder.itemView.findViewById<TextInputLayout>(R.id.studentInputLayout)
        val addStudentButton = holder.itemView.findViewById<Button>(R.id.add_student_button)
        val removeStudentButton = holder.itemView.findViewById<Button>(R.id.remove_student_button)

        studentInputLayout.editText?.setText(students[holder.adapterPosition])
        studentInputLayout.editText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length > 30) {
                    studentInputLayout.isErrorEnabled = true
                    studentInputLayout.error = studentInputLayout.context.resources.getString(R.string.text_too_long)
                } else {
                    studentInputLayout.error = null
                    studentInputLayout.isErrorEnabled = false
                }
                students[holder.adapterPosition] = s.toString()
                activity.updateArray(students)
            }
        })

        addStudentButton.setOnClickListener {
            if (recyclerView.isAnimating) return@setOnClickListener
            val newPosition = holder.adapterPosition + 1
            students.add(newPosition, "")
            notifyItemInserted(newPosition)
        }

        removeStudentButton.setOnClickListener {
            if (students.size <= 1) return@setOnClickListener
            val removePos = holder.adapterPosition
            if (removePos == -1) return@setOnClickListener
            students.removeAt(removePos)
            activity.updateArray(students)
            notifyItemRemoved(removePos)
            prevSize = students.size
        }

        if (students.size > prevSize) {
            //New entry added, request focus to edittext
            studentInputLayout.editText?.requestFocus()
            prevSize = students.size
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = students.size

    fun resizeTo(newSize: Int) {
        if (newSize == students.size) return
        var text = "Resize list to $newSize? You currently have ${students.size} " + (if (students.size == 1) "entry" else "entries") + "."
        if (newSize < students.size) text += " The last ${students.size - newSize} " + (if (students.size - newSize == 1) "entry" else "entries") + " will be removed."
        val builder: AlertDialog.Builder = activity.let {
            AlertDialog.Builder(it)
        }
        builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
            if (newSize > students.size) {
                var counter = students.size
                while (counter < newSize) {
                    students.add("")
                    counter++
                }
                activity.updateArray(students)
                notifyDataSetChanged()
                prevSize = students.size
            } else {
                var counter = students.size
                while (counter > newSize) {
                    students.removeAt(students.size - 1)
                    counter--
                }
                activity.updateArray(students)
                notifyDataSetChanged()
                prevSize = students.size
            }
        }
        builder.setNegativeButton(R.string.dialog_cancel) { _, _ -> }
        builder.setMessage(text).setTitle("Resize")
        builder.show()
    }
}