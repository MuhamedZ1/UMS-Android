package com.example.ums.superAdminCollegeAdminActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.CourseElective
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.NewOpenCoursesForStudentListItemViewAdapter
import com.example.ums.interfaces.ClickListener
import com.example.ums.interfaces.Searchable
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton


class StudentOpenCourseSelectActivity: AppCompatActivity(), ClickListener, Searchable {

    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var toolBar: MaterialToolbar? = null

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true

    private var newCoursesForProfessorListItemViewAdapter : NewOpenCoursesForStudentListItemViewAdapter? = null

    private lateinit var courseDAO: CourseDAO

    private var transactionID: Int? = null
    private var studentID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_item_select_page)
        val arguments = intent.extras
        studentID = arguments?.getInt("student_professional_course_select_activity_student_id")
        transactionID = arguments?.getInt("transaction_id")
        if(savedInstanceState!=null){
            searchQuery = savedInstanceState.getString("student_open_course_select_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("student_open_course_select_activity_is_search_query_open")
        }

        val databaseHelper = DatabaseHelper.newInstance(this)
        courseDAO = CourseDAO(databaseHelper)
        val studentID = studentID
        if(studentID!=null){
            val courseDAO = CourseDAO(databaseHelper)
            val studentDAO = StudentDAO(databaseHelper)
            newCoursesForProfessorListItemViewAdapter = NewOpenCoursesForStudentListItemViewAdapter(studentID,courseDAO, studentDAO, this)
            val recyclerView: RecyclerView = findViewById(R.id.list_view)

            firstTextView = findViewById(R.id.no_items_text_view)
            secondTextView = findViewById(R.id.add_to_get_started_text_view)

            val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
            firstTextView.text = getString(R.string.no_courses_string)
            secondTextView.text = getString(R.string.tap_add_button_to_select_courses_string)
            toolBar = findViewById(R.id.top_app_bar)
            toolBar?.title = getString(R.string.select_courses_string)
            toolBar?.setNavigationOnClickListener {
                if(searchView!=null){
                    if(!searchView!!.isIconified){
                        searchView?.isIconified = true
                        return@setNavigationOnClickListener
                    }
                }
                finish()
            }
            onRefresh()

            initializeSearchView()


            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    onBack()
                }
            })

            addFloatingActionButton.setOnClickListener {
                val student = studentDAO.get(studentID)
                val intent = Intent(this, OtherDepartmentSelectActivity::class.java)
                val bundle = Bundle().apply {
                    putInt("department_id", student?.departmentID ?: return@setOnClickListener)
                    putInt("college_id", student.collegeID)
                    putInt("student_semester", student.semester)
                    putString("student_degree", student.degree)
                    putString("student_elective", CourseElective.OPEN.elective)
                }
                intent.putExtras(bundle)
                startActivity(intent)

            }

            supportFragmentManager.setFragmentResultListener("CourseAddForStudentFragmentPosition", this){_, result->
                val position = result.getInt("id")
                newCoursesForProfessorListItemViewAdapter?.addItem(position)
                onRefresh()
            }

            recyclerView.adapter = newCoursesForProfessorListItemViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun initializeSearchView() {
        searchView = findViewById(R.id.search)
        searchView?.queryHint = getString(R.string.search)
        searchView?.isIconified = isSearchViewOpen
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                searchQuery = p0
                onSearch(p0)
                return false
            }
        })
        if(searchQuery!=null){
            searchView?.setQuery(searchQuery, true)
        }
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(this, StudentProfessorsListActivity::class.java)
        bundle?.putInt("transaction_id", transactionID ?: return)
        intent.putExtras(bundle ?: return)
        resultLauncher.launch(intent)
    }

    private fun onRefresh(){
        if(courseDAO.getNewOpenCourses(studentID).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_courses_string)
            secondTextView.text = getString(R.string.tap_add_button_to_select_courses_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onSearch(query: String?) {
        newCoursesForProfessorListItemViewAdapter?.filter(query)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("student_open_course_select_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("student_open_course_select_activity_is_search_query_open",searchView!!.isIconified)
        }
    }

    private fun onBack(){
        if(searchView!=null){
            if(!searchView!!.isIconified){
                searchView?.isIconified = true
                return
            }
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        newCoursesForProfessorListItemViewAdapter?.updateList()
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data

            val resultIntent = Intent()
            resultIntent.putExtras(data?.extras ?: return@registerForActivityResult)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}