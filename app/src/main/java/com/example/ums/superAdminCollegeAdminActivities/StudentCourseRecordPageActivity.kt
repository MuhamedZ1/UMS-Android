package com.example.ums.superAdminCollegeAdminActivities

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.CompletionStatus
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.bottomsheetdialogs.RecordUpdateBottomSheet
import com.example.ums.model.databaseAccessObject.RecordsDAO
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.example.ums.model.databaseAccessObject.TestDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentCourseRecordPageActivity: AppCompatActivity() {

    private var courseId: Int? = null
    private var studentId: Int? = null
    private var departmentId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_course_record_page)
        val extras = intent.extras
        courseId = extras?.getInt("student_course_record_course_id")
        studentId = extras?.getInt("student_course_record_student_id")
        departmentId = extras?.getInt("student_course_record_department_id")
        val departmentId = departmentId
        val studentId = studentId
        val courseId = courseId
        if(courseId!=null && studentId != null && departmentId!=null){
            val courseNameTextView = findViewById<TextView>(R.id.course_name_text_view)
            val attendanceTextView = findViewById<TextView>(R.id.attendance)
            val internalMarksTextView = findViewById<TextView>(R.id.internal_marks)
            val externalMarksTextView = findViewById<TextView>(R.id.external_marks)
            val assignmentMarksTextView = findViewById<TextView>(R.id.assignment_marks)
            val courseStatusTextView = findViewById<TextView>(R.id.course_status)
            val professorIDTextView = findViewById<TextView>(R.id.professor_id)
            val professorNameTextView = findViewById<TextView>(R.id.professor_name)

            val attendanceIncreaseButton = findViewById<MaterialButton>(R.id.attendance_increase_button)
            val floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
            val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            val databaseHelper = DatabaseHelper.newInstance(this)
            val recordsDAO = RecordsDAO(databaseHelper)
            val testDAO = TestDAO(databaseHelper)

            val studentDAO = StudentDAO(databaseHelper)
            var record = recordsDAO.get(studentId, courseId, departmentId)
            val course = record?.courseProfessor?.course
            val student = studentDAO.get(studentId)
            toolBar.setNavigationOnClickListener {
                finish()
            }

            courseNameTextView.text = course?.name

            attendanceTextView.text = getString(R.string.record_attendance, record?.attendance ?: 0, (record?.attendance ?: 0)*2)
            assignmentMarksTextView.text = getString(R.string.record_assignment, record?.assignmentMarks ?: 0)
            courseNameTextView.text = course?.name
            internalMarksTextView.text = getString(R.string.record_internal_marks, ((record?.attendance ?: 0)/20) + (record?.assignmentMarks ?: 0) +((testDAO.getAverageTestMark(studentId, courseId, departmentId) ?: 0)))
            externalMarksTextView.text = getString(R.string.record_external_marks, record?.externalMarks ?: 0)
            professorIDTextView.text = record?.courseProfessor?.professor?.user?.id.toString()
            professorNameTextView.text = record?.courseProfessor?.professor?.user?.name

            if(course?.semester != null && student?.semester != null){
                courseStatusTextView.text = if(record?.status == CompletionStatus.NOT_COMPLETED.status && (student.semester > course.semester)) getString(
                                    R.string.ongoing_backlog_string) else getString(R.string.ongoing_string)
            }

            attendanceIncreaseButton.setOnClickListener {
                record?.attendance?.let {
                    if(it<50){
                        record?.attendance = it + 1
                        recordsDAO.update(record)
                        attendanceTextView.text = getString(R.string.record_attendance, record?.attendance ?: 0, (record?.attendance ?: 0)*2)
                        internalMarksTextView.text = getString(R.string.record_internal_marks, ((record?.attendance ?: 0)/20) + (record?.assignmentMarks ?: 0) +((testDAO.getAverageTestMark(studentId, courseId, departmentId) ?: 0)))
                    }else{
                        Toast.makeText(this, getString(R.string.maximum_attendance_days_reached_string), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            floatingActionButton.setOnClickListener{
                val bottomSheet = RecordUpdateBottomSheet.newInstance(studentId, courseId, departmentId)
                bottomSheet?.show(supportFragmentManager, "RecordUpdateBottomSheet")
            }

            supportFragmentManager.setFragmentResultListener("RecordUpdateBottomSheerFragment", this){_, _->
                record = recordsDAO.get(studentId, courseId, departmentId)
                attendanceTextView.text = getString(R.string.record_attendance, record?.attendance ?: 0, (record?.attendance ?: 0)*2)
                assignmentMarksTextView.text = getString(R.string.record_assignment, record?.assignmentMarks ?: 0)
                internalMarksTextView.text = getString(R.string.record_internal_marks, ((record?.attendance ?: 0)/20) + (record?.assignmentMarks ?: 0) +((testDAO.getAverageTestMark(studentId, courseId, departmentId) ?: 0)))
                externalMarksTextView.text = getString(R.string.record_external_marks, record?.externalMarks ?: 0)
            }
        }
    }
}