package com.farionik.yandextestapp.view_model

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import com.blankj.utilcode.util.NetworkUtils
import com.farionik.yandextestapp.repository.work_manager.SplashWorkManager
import com.farionik.yandextestapp.ui.AppScreens
import com.github.terrakok.cicerone.Router
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class SplashViewModel(
    context: Context,
    private val router: Router
) : BaseViewModel(context) {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun loadStartData() {
        if (NetworkUtils.isConnected()) {
            val loadDataWorkRequest = OneTimeWorkRequestBuilder<SplashWorkManager>()
                .setConstraints(constraints)
                .build()
            workManager.enqueueUniqueWork(
                "splash_loading_data",
                ExistingWorkPolicy.KEEP,
                loadDataWorkRequest
            )
            workManager.getWorkInfoByIdLiveData(loadDataWorkRequest.id)
                .observeForever {
                    it.let {
                        if (it.state == WorkInfo.State.SUCCEEDED) {
                            router.replaceScreen(AppScreens.mainScreen())
                        }
                    }
                }
        } else {
            router.replaceScreen(AppScreens.mainScreen())
        }
    }
}