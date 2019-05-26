package io.julius.chow.app

import android.app.Application
import io.julius.chow.di.AppComponent
import io.julius.chow.di.AppModule
import io.julius.chow.di.DaggerAppComponent

class App : Application() {

    val appComponent: AppComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        this.injectMembers()
    }

    private fun injectMembers() = appComponent.inject(this)
}
