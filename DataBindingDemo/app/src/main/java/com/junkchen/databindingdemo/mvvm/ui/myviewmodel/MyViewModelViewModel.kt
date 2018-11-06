package com.junkchen.databindingdemo.mvvm.ui.myviewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.junkchen.databindingdemo.entity.User

class MyViewModelViewModel : ViewModel() {
    companion object {
        const val TAG = "MyViewModelViewModel"
    }
    private lateinit var users: MutableLiveData<List<User>>

    fun getUsers(): LiveData<List<User>> {
        Log.i(TAG, "getUsers(): users.isLateinit: ${::users.isInitialized}")
        if (!::users.isInitialized) {
            users = MutableLiveData()
            loadUsers()
        }
        return users
    }

    private fun loadUsers() {
        // Do an asynchronous operation to fetch users.
        val userList = arrayListOf<User>()
        userList.add(User("Junk", "Chen"))
        userList.add(User("Jack", "Ma"))
        userList.add(User("Android", "Google"))
        users.value = userList
    }
}
