package com.example.model

data class BlockedNumber(
    val id: String,
    val phoneNumber: String,
    val name: String,
    val blockedAt: Long = System.currentTimeMillis()
)
