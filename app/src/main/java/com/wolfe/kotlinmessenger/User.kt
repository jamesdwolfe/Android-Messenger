package com.wolfe.kotlinmessenger

class User(val uid: String, val username: String, val profileImageUrl: String) {
    constructor() : this("","","")
}