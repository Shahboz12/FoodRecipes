package com.example.anyrecipe.activities

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.anyrecipe.R
import com.example.anyrecipe.databinding.ActivitySignUpBinding
import com.example.anyrecipe.db.SignupDatabase
import com.example.anyrecipe.db.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SignUpActivity : AppCompatActivity() {
    private lateinit var loginButton: Button
    private lateinit var signupDb : SignupDatabase
    lateinit var binding : ActivitySignUpBinding
    lateinit var videoView: VideoView
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        loginButton = findViewById(R.id.log_btn)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        signupDb = SignupDatabase.getDatabase(this)
        binding.signBtn.setOnClickListener {
            writeData()
        }
        //playing the video in background
        videoView = findViewById(R.id.videoView)

        val videoPath = "android.resource://" + packageName + "/" + R.raw.back_video
        videoView.setVideoURI(Uri.parse(videoPath))
        videoView.start()
        videoView.setOnPreparedListener { mp: MediaPlayer ->
            mp.isLooping = true

        }
        passwordEditText = findViewById(R.id.tv_password)
        confirmPasswordEditText = findViewById(R.id.tv_password_confirm)
        submitButton = findViewById(R.id.log_btn)

        submitButton.setOnClickListener {
            checkPasswordsMatch()
        }

    }
    // Resume the video when the activity is resumed
    override fun onResume() {
        super.onResume()
        videoView.start()
    }
    //checking if passwords match
    private fun checkPasswordsMatch() {
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (password == confirmPassword) {
            checkData()
            submitButton.isEnabled = true
        } else {
            // Passwords do not match
            // Show an error message to the user
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkData() {
        val userName = binding.tvUser.text.toString()
        val password = binding.tvPassword.text.toString()
        // Check if username or password is empty
        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            return
        }

        GlobalScope.launch {
            val user = signupDb.userDao().getUserByUsername(userName)

            // Check if the user exists
            if (user != null) {
                // Check if the password matches
                if (user.password == password) {
                    // Password matches, perform login action
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Finish the current activity to prevent going back
                } else {
                    // Password doesn't match, show error message
                    runOnUiThread {
                        Toast.makeText(this@SignUpActivity, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // User doesn't exist, show error message
                runOnUiThread {
                    Toast.makeText(this@SignUpActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun writeData(){

        val userName = binding.tvUser.text.toString()
        val password = binding.tvPassword.text.toString()

        if(userName.isNotEmpty() && password.isNotEmpty()) {
            val user = User(
                null, userName, password
            )
            GlobalScope.launch(Dispatchers.IO) {
                signupDb.userDao().upsert(user)
            }

            binding.tvUser.text.clear()
            binding.tvPassword.text.clear()
            binding.tvPasswordConfirm.text.clear()

            Toast.makeText(this,"Successfully written",Toast.LENGTH_SHORT).show()
        }else Toast.makeText(this,"PLease Enter Data",Toast.LENGTH_SHORT).show()

    }
}