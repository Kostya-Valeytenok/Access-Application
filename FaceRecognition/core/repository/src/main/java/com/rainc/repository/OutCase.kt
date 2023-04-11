package com.rainc.repository

abstract class OutCase<out O, R:Repository>(repository: R) : UseCase<R>(repository) {

    abstract suspend fun invoke(): O
}
