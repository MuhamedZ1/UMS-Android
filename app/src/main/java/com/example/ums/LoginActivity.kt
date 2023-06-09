package com.example.ums

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.model.User
import com.example.ums.model.databaseAccessObject.UserDAO
import com.example.ums.professorActivities.ProfessorMainPageActivity
import com.example.ums.studentActivities.StudentMainPageActivity
import com.example.ums.superAdminCollegeAdminActivities.SuperAdminCollegeAdminMainPageActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var userDAO: UserDAO


    private var errorText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout_page)


        val databaseHelper = DatabaseHelper.newInstance(this.applicationContext)
        userDAO = UserDAO(databaseHelper)

        errorText = savedInstanceState?.getString("error_text")
        val textViewUserNamePasswordIncorrect = findViewById<TextView>(R.id.textViewUserIDPasswordNotCorrect)
        textViewUserNamePasswordIncorrect?.text = errorText


        val loginButton = findViewById<Button>(R.id.login_button)
        val userIDEditText = findViewById<EditText>(R.id.userIDEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        val sharedPreferences = getSharedPreferences("UMSPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", null)
        val userPassword=  sharedPreferences.getString("password", null)
        val isLoggedOut = sharedPreferences.getBoolean("isLoggedOut", true)

        if(userEmail != null && userPassword != null && !isLoggedOut){
            val user = userDAO.get(userEmail, userPassword)
            goToMainPage(user)
        }
        else{
            loginButton.setOnClickListener {

                textViewUserNamePasswordIncorrect?.text = ""

                val userEmailID = userIDEditText.text.toString().lowercase()
                val password = passwordEditText.text.toString()
                val user = userDAO.get(userEmailID, password)

                if(user == null){

                        if(userEmailID == "" || password == "")
                            textViewUserNamePasswordIncorrect.text = getString(R.string.enter_fields_properly_string)
                        else
                            textViewUserNamePasswordIncorrect.text = getString(R.string.user_email_id_or_password_incorrect_string)


                }
                else{
                    saveUserData(user)
                    goToMainPage(user)
                }
            }
        }
    }

    private fun goToMainPage(user : User?){
        if(UserRole.SUPER_ADMIN.role == user?.role || UserRole.COLLEGE_ADMIN.role == user?.role){
            val intent = Intent(this, SuperAdminCollegeAdminMainPageActivity::class.java)
            val bundle = Bundle()
            bundle.putString("userName", user.name)
            bundle.putInt("userID", user.id)
            bundle.putString("userRole", user.role)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        }
        else if(UserRole.PROFESSOR.role == user?.role){
            val intent = Intent(this, ProfessorMainPageActivity::class.java)
            val bundle = Bundle()
            bundle.putString("userName", user.name)
            bundle.putInt("userID", user.id)
            bundle.putString("userRole", user.role)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        }
        else if(UserRole.STUDENT.role == user?.role){
            val intent = Intent(this, StudentMainPageActivity::class.java)
            val bundle = Bundle()
            bundle.putString("userName", user.name)
            bundle.putInt("userID", user.id)
            bundle.putString("userRole", user.role)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        }
    }

    private fun saveUserData(user : User){
        val editor = getSharedPreferences("UMSPreferences", Context.MODE_PRIVATE).edit()
        editor.putString("email",user.emailID)
        editor.putString("password", user.password)
        editor.putBoolean("isLoggedOut", false)
        editor.apply()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("error_text", errorText)
    }
}