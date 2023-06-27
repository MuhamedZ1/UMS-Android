package com.example.ums.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.AddableSearchableFragment
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.TransactionsListItemViewAdapter
import com.example.ums.bottomsheetdialogs.TransactionAddBottomSheet
import com.example.ums.dialogFragments.TransactionAddConfirmationDialog
import com.example.ums.dialogFragments.TransactionDeleteDialog
import com.example.ums.listener.ItemListener
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.example.ums.model.databaseAccessObject.TransactionDAO

class TransactionFragment: AddableSearchableFragment(), ItemListener {

    private var professorListItemViewAdapter: TransactionsListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var studentID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentID = arguments?.getInt("student_activity_student_id")
        val transactionDAO = TransactionDAO(DatabaseHelper(requireActivity()))
        if(studentID!=null){
            professorListItemViewAdapter = TransactionsListItemViewAdapter(studentID!!, transactionDAO, this )
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_page, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.list_view)

        firstTextView = view.findViewById(R.id.no_items_text_view)
        secondTextView = view.findViewById(R.id.add_to_get_started_text_view)

        onRefresh()
        recyclerView.adapter = professorListItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("TransactionAddFragmentPosition"){_, result->
            val position = result.getInt("id")
            addAt(position)
        }
        setFragmentResultListener("TransactionDeleteDialog"){_, result->
            val id = result.getInt("id")
            professorListItemViewAdapter?.deleteItem(id)
            onRefresh()
        }
        setFragmentResultListener("TransactionAddConfirmationDialog"){_, _ ->
            addTestDialog()
        }
    }
    override fun onUpdate(id: Int) {
//        val collegeAdminUpdateBottomSheet = ProfessorUpdateBottomSheet()
//        collegeAdminUpdateBottomSheet.arguments = Bundle().apply {
//            putInt("college_activity_professor_id", id)
//        }
//        collegeAdminUpdateBottomSheet.show(requireActivity().supportFragmentManager, "collegeAdminUpdateDialog")
    }

    private fun addTestDialog(){
        val transactionAddBottomSheet = TransactionAddBottomSheet()
        transactionAddBottomSheet.arguments = Bundle().apply {
            studentID?.let { studentID -> putInt("student_id", studentID) }
        }
        transactionAddBottomSheet.show(requireActivity().supportFragmentManager, "TransactionAddDialog")
    }

    override fun onDelete(id: Int) {
        val deleteFragment = TransactionDeleteDialog.getInstance(id)
        deleteFragment.show(requireActivity().supportFragmentManager, "TransactionDeleteDialog")
    }

    override fun onClick(bundle: Bundle?) {
//        val intent = Intent(requireContext(), ProfessorCoursesListActivity::class.java)
//        if(bundle!=null){
//            editProfessorId = bundle.getInt("professor_profile_professor_id")
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }
    }

    override fun onAdd() {
        val transactionDAO = TransactionDAO(DatabaseHelper(requireActivity()))
        val studentDAO = StudentDAO(DatabaseHelper(requireActivity()))
        val student = studentDAO.get(studentID) ?: return
        for (transaction in transactionDAO.getList(studentID ?: return)){
            if(transaction.semester == student.semester){
                val transactionAddConfirmationDialog = TransactionAddConfirmationDialog()
                transactionAddConfirmationDialog.show(requireActivity().supportFragmentManager, "TransactionAddConfirmationDialog")
                return
            }
        }
        addTestDialog()
    }

    override fun onSearch(query: String?) {
        professorListItemViewAdapter?.filter(query)
    }

    private fun addAt(id: Int){
        professorListItemViewAdapter?.addItem(id)
        onRefresh()
    }

    private fun onRefresh(){
        val transactionDAO = TransactionDAO(DatabaseHelper(requireActivity()))
        if(transactionDAO.getList(studentID ?: return).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_transactions_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        professorListItemViewAdapter?.updateList()
}
}