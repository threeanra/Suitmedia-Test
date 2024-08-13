package com.threeanra.interntest.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.threeanra.interntest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            checkBtn.setOnClickListener {
            val palindrome = binding.edPalindrome.text.toString().lowercase().replace(" ", "")
                if(palindrome.isNotEmpty()) {
                    if (palindrome == palindrome.reversed()) {
                        showToast("isPalindrome")
                    } else {
                        showToast("not palindrome")
                    }
                } else {
                    showToast("Please enter text")
                }
            }
            nextBtn.setOnClickListener {
                val name = binding.edName.text.toString()
                if(name.isNotEmpty()) {
                    val intent = Intent(this@MainActivity, SecondScreenActivity::class.java)
                    intent.putExtra(SecondScreenActivity.NAME, name)
                    startActivity(intent)
                } else {
                    showToast("Please enter your name")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}