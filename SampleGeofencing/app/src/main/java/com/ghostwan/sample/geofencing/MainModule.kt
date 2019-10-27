package com.ghostwan.sample.geofencing

import org.koin.dsl.module

val mainModule = module {
    single<MainContract.Repository> { SharePreferenceRepository() }
    factory<MainContract.Presenter> { MainPresenter(get(), get()) }
}