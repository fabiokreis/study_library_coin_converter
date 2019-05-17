package br.com.studylibrary.models

data class ExchangeRates(
    val base: String = "",
    val rates: Map<String, Double> = mutableMapOf(),
    val date: String = ""
)
