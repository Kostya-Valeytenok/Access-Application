package com.psu.accessapplication.di.modules

import com.psu.accessapplication.AnalyzeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val vmModule = module {
    viewModel { AnalyzeViewModel(get()) }
}