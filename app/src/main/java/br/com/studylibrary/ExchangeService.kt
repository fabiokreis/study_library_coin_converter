package br.com.studylibrary

import br.com.studylibrary.models.ExchangeRates
import retrofit2.Call
import retrofit2.http.GET

interface ExchangeService {

    companion object {
        const val BASE_URL = "https://api.exchangeratesapi.io/"
    }

    @GET("latest?base=BRL&symbols=USD,GBP,EUR")
    fun listExchange(): Call<ExchangeRates>

}