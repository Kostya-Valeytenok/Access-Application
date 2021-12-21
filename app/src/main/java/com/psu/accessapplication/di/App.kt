package com.psu.accessapplication.di

import android.app.Application
import android.content.Context
import com.psu.accessapplication.model.FaceModel
import com.psu.accessapplication.model.Person
import com.psu.accessapplication.repository.AppDatabase
import com.psu.accessapplication.tools.AbstractPreferences
import com.psu.accessapplication.tools.CoroutineWorker
import com.psu.accessapplication.tools.PersonDataCache
import com.psu.accessapplication.tools.Preferences
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject lateinit var bd: AppDatabase

    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CoroutineWorker.launch {
            if (Preferences.isFirstLaunch) {
                Preferences.isFirstLaunch = false
                getPersons().forEach {
                    bd.persons().insert(it)
                }
            }
            bd.persons().allRX.collect {
                PersonDataCache.init(it)
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        AbstractPreferences.init(base)
        super.attachBaseContext(base)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getPersons() = buildList<Person> {
        add(
            Person(
                firstName = "Olga",
                secondName = "Mentova",
                personImageUrl = "https://shpilki.net/wp-content/uploads/ryzhevolosym-devushkam-dlya-tatuazha-mozhno-smelo-vybirat-teplye-ottenki-korichnevogo.jpg",
                face = FaceModel(
                    eyesDistance = 96.991,
                    lEyeAndMouseDistance = 86.569,
                    rEyeAndMouseDistance = 88.373,
                    mouthWidth = 50.817,
                    noseAndMouseDistance = 55.812,
                    lEyeAndNoseDistance = 39.44,
                    rEyeAndNoseDistance = 65.43
                )
            )
        )

        add(
            Person(
                firstName = "Nila",
                secondName = "Melon",
                personImageUrl = "https://i.pinimg.com/originals/c5/db/e1/c5dbe1565cf7452bf56cecbab1d062d4.jpg",
                face = FaceModel(
                    eyesDistance = 109.80,
                    lEyeAndMouseDistance = 102.22,
                    rEyeAndMouseDistance = 89.37,
                    mouthWidth = 47.598,
                    noseAndMouseDistance = 44.863,
                    rEyeAndNoseDistance = 48.48,
                    lEyeAndNoseDistance = 82.625,
                )
            )
        )
        add(
            Person(
                firstName = "Alla",
                secondName = "Terrova",
                personImageUrl = "https://pbs.twimg.com/media/Dx-8TiWX0AAZOpw.jpg:large",
                face = FaceModel(
                    eyesDistance = 100.088,
                    lEyeAndMouseDistance = 80.887,
                    rEyeAndMouseDistance = 85.277,
                    mouthWidth = 47.620,
                    noseAndMouseDistance = 46.182,
                    rEyeAndNoseDistance = 45.198,
                    lEyeAndNoseDistance = 64.809
                )
            )
        )
        add(
            Person(
                firstName = "Lilia",
                secondName = "Navi",
                personImageUrl = "https://www.meme-arsenal.com/memes/b08d2860e80a1e124997a1fc0b16093a.jpg",
                face = FaceModel(
                    eyesDistance = 102.779,
                    lEyeAndMouseDistance = 96.94,
                    rEyeAndMouseDistance = 90.334,
                    mouthWidth = 51.73,
                    noseAndMouseDistance = 44.15,
                    rEyeAndNoseDistance = 51.981,
                    lEyeAndNoseDistance = 72.531
                )
            )
        )

        add(
            Person(
                firstName = "Lena",
                secondName = "Kosta",
                personImageUrl = "https://avatars.mds.yandex.net/get-zen_doc/249065/pub_5ce393e9da7a8100b3ddbaf5_5ce39b780a0d8b00b24d1d5c/scale_1200",
                face = FaceModel(
                    eyesDistance = 112.62,
                    lEyeAndMouseDistance = 99.96,
                    rEyeAndMouseDistance = 102.88,
                    mouthWidth = 54.33,
                    noseAndMouseDistance = 50.83,
                    rEyeAndNoseDistance = 59.029,
                    lEyeAndNoseDistance = 73.618
                )
            )
        )
    }
}
