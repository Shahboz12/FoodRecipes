package com.example.anyrecipe.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [User :: class], version = 1)
abstract class SignupDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    companion object{

        @Volatile
        private var INSTANCE : SignupDatabase? = null

        fun getDatabase(context: Context): SignupDatabase{
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    SignupDatabase:: class.java,
                    "sign_up.db"
                ).fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE as SignupDatabase
        }
    }
}