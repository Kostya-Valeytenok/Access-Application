package com.rainc.firebaseglobal.model

data class GenericError(
    val code: Int? = null,
    val error: String? = null,
    val exception: Exception? = null): Throwable(error, exception)