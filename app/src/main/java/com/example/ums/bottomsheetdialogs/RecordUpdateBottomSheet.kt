package com.example.ums.bottomsheetdialogs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.RecordsDAO
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class RecordUpdateBottomSheet: FullScreenBottomSheetDialog() {

    private lateinit var attendance : TextInputLayout
    private lateinit var externalMarks : TextInputLayout
    private lateinit var assignment : TextInputLayout

    private var studentID: Int? = null
    private var courseID: Int? = null
    private var departmentID: Int? = null

    private lateinit var attendanceText: String
    private lateinit var externalMarksText: String
    private lateinit var assignmentText: String

    private var attendanceError: String? = null
    private var externalMarksError: String? = null
    private var assignmentError: String? = null

    companion object{
        fun newInstance(studentID: Int?, courseID: Int?, departmentID: Int?): RecordUpdateBottomSheet?{
            return RecordUpdateBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt("record_update_student_id", studentID ?: return null)
                    putInt("record_update_course_id", courseID ?: return null)
                    putInt("record_update_department_id", departmentID ?: return null)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        studentID = arguments?.getInt("record_update_student_id")
        courseID = arguments?.getInt("record_update_course_id")
        departmentID = arguments?.getInt("record_update_department_id")

        externalMarksText = savedInstanceState?.getString("record_update_external_marks_text") ?: ""
        assignmentText = savedInstanceState?.getString("record_update_assignment_text") ?: ""
        attendanceText = savedInstanceState?.getString("record_update_attendance_text") ?: ""

        assignmentError = savedInstanceState?.getString("record_update_assignment_error")
        externalMarksError = savedInstanceState?.getString("record_update_external_marks_error")
        attendanceError = savedInstanceState?.getString("record_update_attendance_error")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_course_record_update_page, container, false)
        val bottomSheetCloseButton =
            view.findViewById<ImageButton>(R.id.close_button)

        bottomSheetCloseButton!!.setOnClickListener {
            dismiss()
        }

        attendance =
            view.findViewById(R.id.attendance_layout)
        assignment =
            view.findViewById(R.id.assignment_layout)
        externalMarks =
            view.findViewById(R.id.external_mark_layout)

        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        val recordsDAO = RecordsDAO(databaseHelper)
        val record = recordsDAO.get(studentID, courseID, departmentID)
        if(attendanceText.isNotEmpty()){
            attendance.editText?.setText(attendanceText)
        }
        else{
            record?.attendance?.let { attendance.editText?.setText(it.toString()) }
        }
        if(assignmentText.isNotEmpty()){
            assignment.editText?.setText(assignmentText)
        }
        else{
            record?.assignmentMarks?.let { assignment.editText?.setText(it.toString()) }
        }
        if(externalMarksText.isNotEmpty()){
            externalMarks.editText?.setText(externalMarksText)
        }
        else{
            record?.externalMarks?.let { externalMarks.editText?.setText(it.toString()) }
        }

        val updateButton =
            view.findViewById<MaterialButton>(R.id.update_button)
        updateButton.isEnabled = false
        if(attendance.editText?.text.toString() != record?.attendance.toString() ||
                assignment.editText?.text.toString() != record?.assignmentMarks.toString() ||
                externalMarks.editText?.text.toString() != record?.externalMarks.toString()){
            updateButton.isEnabled = true
        }
        setView(view)

        attendance.editText?.addTextChangedListener(textListener(attendance, {
            attendanceError = null
        }, record?.attendance.toString(), updateButton))
        assignment.editText?.addTextChangedListener(textListener(assignment, {
            assignmentError = null
        }, record?.assignmentMarks.toString(), updateButton))
        externalMarks.editText?.addTextChangedListener(textListener(externalMarks, {
            externalMarksError = null
        }, record?.externalMarks.toString(), updateButton))


        updateButton.setOnClickListener {
            var flag = true

            attendanceText = attendance.editText?.text.toString()
            assignmentText = assignment.editText?.text.toString()
            externalMarksText = externalMarks.editText?.text.toString()

            if (attendanceText.isEmpty()) {
                flag = false
                attendanceError = getString(R.string.don_t_leave_attendance_field_blank_string)
                attendance.error = attendanceError
            }
            else if(attendanceText.toInt() !in 0..50){
                flag = false
                attendanceError = getString(R.string.enter_attendance_below_50_string)
                attendance.error = attendanceError
            }
            if (assignmentText.isEmpty()) {
                flag = false
                assignmentError = getString(R.string.don_t_leave_assignment_field_blank_string)
                assignment.error = assignmentError
            }
            else if(assignmentText.toInt() !in 0..10){
                flag = false
                assignmentError = getString(R.string.enter_attendance_equal_to_or_below_10_string)
                assignment.error = assignmentError
            }
            if (externalMarksText.isEmpty()) {
                flag = false
                externalMarksError = getString(R.string.don_t_leave_external_marks_field_blank_string)
                externalMarks.error = externalMarksError
            }
            else if(externalMarksText.toInt() !in 0..60){
                flag = false
                externalMarksError = getString(R.string.enter_external_marks_below_60_string)
                externalMarks.error = externalMarksError
            }

            if (flag) {
                val updatedRecord = recordsDAO.get(studentID, courseID, departmentID)
                updatedRecord?.attendance = attendanceText.toInt()
                updatedRecord?.assignmentMarks = assignmentText.toInt()
                updatedRecord?.externalMarks = externalMarksText.toInt()
                recordsDAO.update(updatedRecord)

                setView(view)
                Toast.makeText(requireContext(), getString(R.string.record_updated_string), Toast.LENGTH_SHORT).show()
                setFragmentResult("RecordUpdateBottomSheerFragment", bundleOf())
                dismiss()
            }

        }
        return view
    }

    override fun onStop() {
        super.onStop()
        clearErrors()
    }

    private fun clearErrors(){
        attendance.error = null
        assignment.error = null
        externalMarks.error = null
    }

    private fun setView(view : View){
        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        val studentDAO = StudentDAO(databaseHelper)
        val courseDAO = CourseDAO(databaseHelper)
        val student = studentDAO.get(studentID)
        val course = courseDAO.get(courseID, departmentID, student?.collegeID)
        view.findViewById<TextView>(R.id.course_name_text_view)?.text = course?.name
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("record_update_attendance_text",attendance.editText?.text.toString())
        outState.putString("record_update_external_marks_text", externalMarks.editText?.text.toString())
        outState.putString("record_update_assignment_text", assignment.editText?.text.toString())

        outState.putString("record_update_attendance_error", attendanceError)
        outState.putString("record_update_external_marks_error", externalMarksError)
        outState.putString("record_update_assignment_error", assignmentError)

        externalMarks.error = externalMarksError
        assignment.error = assignmentError
        attendance.error = attendanceError
    }

    private fun textListener(layout: TextInputLayout, errorOperation: (() -> Unit), recordDetail: String, button: MaterialButton): TextWatcher {
        return object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                layout.error = null
                errorOperation()
                button.isEnabled = p0?.toString() != recordDetail
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
    }
}