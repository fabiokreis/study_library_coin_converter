package br.com.studylibrary

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import br.com.studylibrary.models.ExchangeRates
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.objectbox.query.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import trikita.anvil.BaseDSL

import trikita.anvil.DSL.*
import trikita.anvil.RenderableView
import java.util.*

class MainActivity : AppCompatActivity() {

    private var enterValue: Double = 0.0
    private var rateUSD: Double = 0.0
    private var rateEUR: Double = 0.0

    private lateinit var notesBox: Box<Note>
    private lateinit var notesQuery: Query<Note>

    lateinit var service: ExchangeService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getView())

        //ObjectBox
        ObjectBox.init(this)
        notesBox = ObjectBox.boxStore.boxFor()
        notesQuery = notesBox.query { order(Note_.date) }
        updateNotes()

        //Retrofit
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(ExchangeService.BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()

        service = retrofit.create(ExchangeService::class.java)


    }

    private fun getView(): View {
        return object : RenderableView(this) {
            override fun view() {
                linearLayout {
                    size(MATCH, MATCH)
                    padding(dip(8))
                    orientation(LinearLayout.VERTICAL)

                    editText{
                        size(MATCH, WRAP)
                        hint("Digite o valor em Real")
                        if (enterValue > 0.0) text("$enterValue")
                        onTextChanged {
                            it.toString().toDoubleOrNull()?.let { value ->
                                enterValue = value
                            }
                        }
                    }

                    button{
                        size(MATCH, WRAP)
                        text("Salvar")
                        onClick {
                            service.listExchange().enqueue(object : Callback<ExchangeRates>{
                                override fun onFailure(call: Call<ExchangeRates>, t: Throwable) {
                                    Log.e("MainActivity", "Falha no CallBack", t)
                                }

                                override fun onResponse(call: Call<ExchangeRates>, response: Response<ExchangeRates>) {
                                    if (response.isSuccessful) {
                                        response.body()?.let { value ->
                                            addNote(value)
                                        }
                                    }
                                }

                            })
                        }
                    }

                    textView{
                        size(MATCH, WRAP)
                        text("BRL: ${enterValue}")
                    }

                    textView{
                        size(MATCH, WRAP)
                        text("USD: ${enterValue*rateUSD}")
                    }

                    textView{
                        size(MATCH, WRAP)
                        text("EUR: ${enterValue*rateEUR}")
                    }

                    textView{
                        size(MATCH, WRAP)
                        text("Date: ${Date()}")
                    }





                }
            }

        }
    }



    private fun updateNotes(){
        notesQuery.findFirst()?.let {
            enterValue = it.enterValue ?: 0.0
            rateUSD = it.rateUSD ?: 0.0
            rateEUR = it.rateEUR ?: 0.0
        }

    }

    private fun addNote(exchangeRates: ExchangeRates){
        rateUSD = exchangeRates.rates["USD"] ?: 0.0
        rateEUR = exchangeRates.rates["EUR"] ?: 0.0

        val note = Note(enterValue = enterValue, rateUSD = rateUSD, rateEUR = rateEUR , date = Date())

        notesBox.removeAll()
        notesBox.put(note)

        updateNotes()
    }

}
