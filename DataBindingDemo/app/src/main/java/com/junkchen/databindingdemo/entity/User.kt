package com.junkchen.databindingdemo.entity

data class User(val firstName: String, val lastName: String) {
    fun getName() = "$firstName $lastName"
}