package com.example.contactreader

data class Contact(
    val id: String,
    val name: String?,
    val photo: String?,
    val emails: MutableList<Email> = mutableListOf(),
    val numbers: MutableList<Number> = mutableListOf()
)

data class Email(
    val type: String,
    val email: String
)

data class Number(
    val type: String,
    val number: String
)