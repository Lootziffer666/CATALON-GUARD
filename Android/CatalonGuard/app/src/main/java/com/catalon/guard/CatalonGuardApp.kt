package com.catalon.guard

import android.app.Application
import com.catalon.guard.data.local.db.DatabaseInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CatalonGuardApp : Application() {

    @Inject lateinit var databaseInitializer: DatabaseInitializer

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            databaseInitializer.initializeIfNeeded()
        }
    }
}
