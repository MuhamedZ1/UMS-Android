package com.example.ums

import android.app.Activity
import android.util.Log
import com.example.ums.model.databaseAccessObject.UserDAO

class Utility {
    companion object{
        fun isValidContactNumber(contactNumber : String): Boolean {
            val number = contactNumber.replace("\\D".toRegex(), "")
            return number.length == 10
        }

        fun isEmailAddressFree(email: String, context: Activity): Boolean{
            val userDAO = UserDAO(DatabaseHelper(context))
            val userList = userDAO.getList()
            for (user in userList){
                if(user.emailID.trim()==email.trim()){
                    return false
                }
            }
            return true
        }

        fun isEmailDotCom(string: String): Boolean {
            val pattern = Regex("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.com")
            return pattern.matches(string.trim())
        }

        fun isCorrectDateFormat(dateString: String): Boolean {
            Log.i("UtilityClass","dateString: $dateString")
            val regex = Regex("""^\d{4}-(0[1-9]|1[0-9]|2[0-9]|3[01])-([1-9]|1[0-2])$""")
            return regex.matches(dateString)

        }
    }
}