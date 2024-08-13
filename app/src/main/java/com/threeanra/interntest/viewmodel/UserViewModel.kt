package com.threeanra.interntest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threeanra.interntest.data.ResultState
import com.threeanra.interntest.data.network.response.DataItem
import com.threeanra.interntest.data.network.retrofit.ApiConfig
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _users = MutableLiveData<ResultState<List<DataItem>>>()
    val users: LiveData<ResultState<List<DataItem>>> = _users

    fun getUsers(page: Int, perPage: Int) {
        viewModelScope.launch {
            _users.value = ResultState.Loading
            try {
                val response = ApiConfig.getApiService().getUsers(page, perPage)
                if (response.isSuccessful) {
                    val userList = response.body()?.data?.filterNotNull() ?: emptyList()
                    _users.value = ResultState.Success(userList)
                } else {
                    _users.value = ResultState.Error(response.message().toString())
                }
            } catch (e: Exception) {
                _users.value = ResultState.Error(e.message.toString())
            }
        }
    }
}
