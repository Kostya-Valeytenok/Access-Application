package com.rainc.repository

abstract class InOutCase<in V, out O, R: Repository>(repository: R) : UseCase<R>(repository){

    abstract suspend operator fun invoke(value: V): O
}
