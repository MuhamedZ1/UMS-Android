package com.example.ums

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class DatabaseHelper(context: Activity) : SQLiteOpenHelper(context, "ums.db",null,1){

    private val createTableCollege = "CREATE TABLE IF NOT EXISTS \"COLLEGE\" (\n" +
            "\t\"C_ID\"\tINTEGER PRIMARY KEY,\n" +
            "\t\"C_NAME\"\tTEXT,\n" +
            "\t\"C_ADDRESS\"\tTEXT,\n" +
            "\t\"C_TELEPHONE\"\tTEXT\n" +
            ")"

    private val createTableCollegeAdmin = "CREATE TABLE IF NOT EXISTS \"COLLEGE_ADMIN\" (\n" +
            "\t\"CA_ID\"\tTEXT,\n" +
            "\t\"COLLEGE_ID\"\tTEXT,\n" +
            "\tPRIMARY KEY(\"CA_ID\")\n" +
            ")"

    private val createTableCourse = "CREATE TABLE IF NOT EXISTS \"COURSE\" (\n" +
            "\t\"COURSE_ID\"\tTEXT,\n" +
            "\t\"COURSE_NAME\"\tTEXT,\n" +
            "\t\"COURSE_SEM\"\tINTEGER,\n" +
            "\t\"DEPT_ID\"\tTEXT,\n" +
            "\t\"COLLEGE_ID\"\tTEXT,\n" +
            "\t\"DEGREE\"\tTEXT,\n" +
            "\t\"ELECTIVE\"\tTEXT,\n" +
            "\tPRIMARY KEY(\"COURSE_ID\",\"DEPT_ID\",\"COLLEGE_ID\")\n" +
            ")"

    private val createTableCourseProfessor = "CREATE TABLE IF NOT EXISTS \"COURSE_PROFESSOR_TABLE\" (\n" +
            "\t\"PROF_ID\"\tTEXT,\n" +
            "\t\"COURSE_ID\"\tTEXT,\n" +
            "\t\"DEPT_ID\"\tTEXT,\n" +
            "\t\"COLLEGE_ID\"\tTEXT,\n" +
            "\tPRIMARY KEY(\"PROF_ID\",\"COURSE_ID\",\"DEPT_ID\",\"COLLEGE_ID\")\n" +
            ")"

    private val createTableDepartment = "CREATE TABLE IF NOT EXISTS \"DEPARTMENT\" (\n" +
            "\t\"DEPT_ID\"\tTEXT,\n" +
            "\t\"DEPT_NAME\"\tTEXT,\n" +
            "\t\"COLLEGE_ID\"\tTEXT,\n" +
            "\tPRIMARY KEY(\"DEPT_ID\",\"COLLEGE_ID\")\n" +
            ")"

    private val createTableProfessor = "CREATE TABLE IF NOT EXISTS \"PROFESSOR\" (\n" +
            "\t\"PROF_ID\"\tTEXT,\n" +
            "\t\"DEPT_ID\"\tTEXT,\n" +
            "\t\"COLLEGE_ID\"\tTEXT,\n" +
            "\tPRIMARY KEY(\"PROF_ID\")\n" +
            ")"

    private val createTableRecords = "CREATE TABLE IF NOT EXISTS \"RECORDS\" (\n" +
            "\t\"STUDENT_ID\"\tTEXT,\n" +
            "\t\"COURSE_ID\"\tTEXT,\n" +
            "\t\"DEPT_ID\"\tTEXT,\n" +
            "\t\"PROF_ID\"\tTEXT,\n" +
            "\t\"COLLEGE_ID\"\tTEXT,\n" +
            "\t\"TRANSACT_ID\"\tTEXT,\n" +
            "\t\"EXT_MARK\"\tINTEGER,\n" +
            "\t\"ATTENDANCE\"\tINTEGER,\n" +
            "\t\"ASSIGNMENT\"\tINTEGER,\n" +
            "\t\"STATUS\"\tTEXT,\n" +
            "\t\"SEM_COMPLETED\"\tINTEGER,\n" +
            "\tPRIMARY KEY(\"TRANSACT_ID\",\"STUDENT_ID\",\"COURSE_ID\",\"DEPT_ID\",\"COLLEGE_ID\")\n" +
            ")"

    private val createTableSection = "CREATE TABLE IF NOT EXISTS \"SECTION\" (\n" +
            "\t\"SEC_ID\"\tTEXT,\n" +
            "\t\"SEC_NAME\"\tTEXT,\n" +
            "\t\"DEPT_ID\"\tTEXT,\n" +
            "\t\"COLLEGE_ID\"\tTEXT,\n" +
            "\tPRIMARY KEY(\"SEC_ID\",\"DEPT_ID\",\"COLLEGE_ID\")\n" +
            ")"

    private val createTableStudent = "CREATE TABLE IF NOT EXISTS \"STUDENT\" (\n" +
            "\t\"STUDENT_ID\"\tTEXT,\n" +
            "\t\"S_SEM\"\tINTEGER,\n" +
            "\t\"S_DEGREE\"\tTEXT,\n" +
            "\t\"SEC_ID\"\tTEXT,\n" +
            "\t\"DEPT_ID\"\tTEXT,\n" +
            "\t\"COLLEGE_ID\"\tTEXT,\n" +
            "\tPRIMARY KEY(\"STUDENT_ID\")\n" +
            ")"

    private val createTableSuperAdmin = "CREATE TABLE IF NOT EXISTS \"SUPER_ADMIN\" (\n" +
            "\t\"SA_ID\"\tTEXT,\n" +
            "\tPRIMARY KEY(\"SA_ID\")\n" +
            ")"

    private val createTableTests = "CREATE TABLE IF NOT EXISTS \"TEST\" (\n" +
            "\t\"TEST_ID\"\tTEXT,\n" +
            "\t\"STUDENT_ID\"\tTEXT,\n" +
            "\t\"COURSE_ID\"\tTEXT,\n" +
            "\t\"DEPT_ID\"\tTEXT,\n" +
            "\t\"COLLEGE_ID\"\tTEXT,\n" +
            "\t\"TEST_MARKS\"\tINTEGER,\n" +
            "\tPRIMARY KEY(\"TEST_ID\",\"STUDENT_ID\",\"COURSE_ID\",\"DEPT_ID\",\"COLLEGE_ID\")\n" +
            ")"

    private val createTableTransactions = "CREATE TABLE IF NOT EXISTS \"TRANSACTIONS\" (\n" +
            "\t\"T_ID\"\tTEXT,\n" +
            "\t\"STUDENT_ID\"\tTEXT,\n" +
            "\t\"T_DATE\"\tDATE,\n" +
            "\t\"T_AMOUNT\"\tINT,\n" +
            "\tPRIMARY KEY(\"T_ID\")\n" +
            ")"


    private val createTableUser = "CREATE TABLE IF NOT EXISTS \"USER\" (\n" +
            "\t\"U_ID\"\tTEXT,\n" +
            "\t\"U_NAME\"\tTEXT,\n" +
            "\t\"U_CONTACT\"\tTEXT,\n" +
            "\t\"U_DOB\"\tDATE,\n" +
            "\t\"U_GENDER\"\tTEXT,\n" +
            "\t\"U_ADDRESS\"\tTEXT,\n" +
            "\t\"U_PASSWORD\"\tTEXT,\n" +
            "\t\"U_ROLE\"\tTEXT,\n" +
            "\t\"U_EMAIL_ID\"\tTEXT,\n" +
            "\tPRIMARY KEY(\"U_ID\")\n" +
            ")"

    private val createStarterSuperAdmin = "INSERT OR IGNORE INTO USER VALUES (\"23-SA-1\",\"AAA\",\"9090909090\",\"2001-01-01\",\"M\",\"CHENNAI\",\"EASY\",\"SUPER_ADMIN\",\"aaa@email.com\")"

    override fun onCreate(database: SQLiteDatabase) {

        database.execSQL(createTableCollege)
        database.execSQL(createTableCollegeAdmin)
        database.execSQL(createTableCourse)
        database.execSQL(createTableCourseProfessor)
        database.execSQL(createTableDepartment)
        database.execSQL(createTableProfessor)
        database.execSQL(createTableRecords)
        database.execSQL(createTableSection)
        database.execSQL(createTableStudent)
        database.execSQL(createTableSuperAdmin)
        database.execSQL(createTableTests)
        database.execSQL(createTableTransactions)
        database.execSQL(createTableUser)

//        if(this.starterSuperAdminUser != null){
            database.execSQL(createStarterSuperAdmin)
//        }

    }

    override fun onUpgrade(sqliteDatabase: SQLiteDatabase?, p1: Int, p2: Int) {
    }
}