package br.com.studylibrary

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*


@Entity
data class Note(
    @Id var id: Long = 0,
    val enterValue: Double? = 0.0,
    val rateUSD: Double? = 0.0,
    val rateEUR: Double? = 0.0,
    val date: Date? = null
)