package bombbird.com.classlist2

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout

class StudentRecyclerAdapter(private val students: ArrayList<String>, private val activity: EditClassActivity):
        RecyclerView.Adapter<StudentRecyclerAdapter.StudentRecyclerViewHolder>() {

    class StudentRecyclerViewHolder(view: View):
        RecyclerView.ViewHolder(view) {
    }

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

        studentInputLayout.editText?.setText(students[0])
        studentInputLayout.editText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length > 30) {
                    studentInputLayout.isErrorEnabled = true
                    studentInputLayout.error = studentInputLayout.context.resources.getString(R.string.text_too_long)
                    //updateTopMargins(addStudentButton, -10)
                    //updateTopMargins(removeStudentButton, -10)
                } else {
                    studentInputLayout.error = null
                    studentInputLayout.isErrorEnabled = false
                    //updateTopMargins(addStudentButton, -0)
                    //updateTopMargins(removeStudentButton, -0)
                }
            }
        })

        addStudentButton.setOnClickListener {
            students.add(position + 1, "")
            activity.updateAdapter(students)
        }

        removeStudentButton.setOnClickListener {
            if (students.size <= 1) return@setOnClickListener
            students.removeAt(position)
            activity.updateAdapter(students)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = students.size

    fun updateTopMargins(view: View, dp: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        layoutParams.topMargin = dp
        view.layoutParams = layoutParams
        view.requestLayout()
    }
}