package com.threeanra.interntest.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.threeanra.interntest.R
import com.threeanra.interntest.adapter.UserAdapter
import com.threeanra.interntest.data.ResultState
import com.threeanra.interntest.databinding.ActivityThirdScreenBinding
import com.threeanra.interntest.viewmodel.UserViewModel

class ThirdScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThirdScreenBinding
    private lateinit var userAdapter: UserAdapter
    private val viewModel: UserViewModel by viewModels()

    private var page = 1
    private var isLoading = false
    private val perPage = 6 // Show only 6 users at a time (need more users for pagination to see more loading, as total users are only 12)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThirdScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTransparentStatusBar()

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            swipeRefreshLayout.setOnRefreshListener {
                page = 1
                userAdapter.submitList(emptyList()) // Clear the list for refresh
                getUsers(true)
            }
        }

        setUsers()

        viewModel.users.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.apply {
                        progressBar.visibility = if (page == 1 && !swipeRefreshLayout.isRefreshing) View.VISIBLE else View.GONE
                        loadRv.visibility = if (page > 1) View.VISIBLE else View.GONE
                    }
                }
                is ResultState.Success -> {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        loadRv.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                    }

                    isLoading = false
                    val data = result.data
                    if (page == 1) {
                        userAdapter.submitList(data)
                    } else {
                        userAdapter.submitList(userAdapter.currentList + data)
                    }

                    if (data.isEmpty() && page == 1) {
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.rvUser.visibility = View.GONE
                    } else {
                        binding.tvEmpty.visibility = View.GONE
                        binding.rvUser.visibility = View.VISIBLE
                    }
                }
                is ResultState.Error -> {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        loadRv.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                    }
                    isLoading = false
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Load initial data
        getUsers(false)
    }

    private fun setUsers() {
        userAdapter = UserAdapter { fullname ->
            val resultIntent = Intent()
            resultIntent.putExtra(UserAdapter.FULLNAME, fullname)
            setResult(UserAdapter.RESULT_CODE, resultIntent)
            finish()
        }

        binding.rvUser.apply {
            layoutManager = LinearLayoutManager(this@ThirdScreenActivity)
            adapter = userAdapter
        }

        // Pagination
        binding.rvUser.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && (visibleItemCount + pastVisibleItems >= totalItemCount)) {
                    page++
                    getUsers(false)
                    if (page > 1) {
                        binding.loadRv.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setTransparentStatusBar() {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColor = ContextCompat.getColor(this@ThirdScreenActivity, R.color.white)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun getUsers(refresh: Boolean) {
        isLoading = true
        viewModel.getUsers(page, perPage)
    }
}

