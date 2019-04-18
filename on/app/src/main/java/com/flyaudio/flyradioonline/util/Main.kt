package com.flyaudio.flyradioonline.util

import android.content.Context
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class Main {
    fun main(){
        doAsync(){
            Request("www.baidu.com").run()
            uiThread {  }
        }

        val f1 = Forecast(Date(),100f,"jaj")
        val f2 = f1.copy(temperature = 20f)
        val(date,temp,detail) = f1

    }
}
