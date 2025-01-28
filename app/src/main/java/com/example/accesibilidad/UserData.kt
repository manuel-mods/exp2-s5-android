package com.example.accesibilidad

data class User(val username: String, val password: String)

object UserData {
    val users = mutableListOf<User>()

    fun addUser(user: User) {
        users.add(user)
    }
}
