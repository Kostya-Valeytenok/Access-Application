package com.psu.accessapplication.di.modules

import com.psu.accessapplication.domain.VerificationUserUseCase
import com.psu.accessapplication.tools.PersonVerification
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelsModule {

    @Provides
    fun createVerificationUserUseCase(verifier: PersonVerification): VerificationUserUseCase {
        return VerificationUserUseCase(verifier)
    }
}
