package com.example.kianarag

import android.app.Application
import com.example.kianarag.di.ragModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KianaRAGApplication : Application() {
    override fun onCreate() {
        super.onCreate()
//        startKoin {
//            androidContext(this@KianaRAGApplication)
//            modules(
//                ragModule,
//            )
//        }
    }
}