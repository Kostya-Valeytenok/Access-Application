package com.rainc.random

import com.rainc.coroutinecore.extension.repeatParallel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class KeyGenerator internal constructor(private val randomCore: RandomCore) {
    private val dictionary = charArrayOf(
        'q','w','e','r','t','y','u' ,'i','o','p',
        'a','s', 'd','f','g','h','j','k','l','z','x','c','v','b','n','m',
        //26
        'Q','W','E','R','T','Y','U','I','O','P'
        ,'A','S','D','F','G','H','J','K','L', 'Z','X','C','V','B','N','M',
        //52
        '1','2','3','4','5','6','7','8','9',
        //61
        '!','@','#','№','$','%','^',':',';','\'','[',']','{','}','?','*','(',')','-','_','+','=',',','<','>', '±' ,'&','`','§',
        '~',
        '.'
        //92
    )
    private val symbolRange = 0..51
    private val numbersRange = 52..60
    private val symbolAndNumbersRange = 0..60
    private val specialSymbolsRange = 61..91
    private val protectedRange = 0..91

    enum class PasswordType{
        NumberPassword,
        FullRegisterSymbolPassword,
        ProtectedPassword,
        FullRegisterSymbolWithNumbersPassword
    }

    private fun PasswordType.passwordRange(): IntRange {
       return when(this){
            PasswordType.NumberPassword -> numbersRange
            PasswordType.FullRegisterSymbolPassword -> symbolRange
            PasswordType.ProtectedPassword -> protectedRange
            PasswordType.FullRegisterSymbolWithNumbersPassword -> symbolAndNumbersRange
       }
    }

    suspend fun generate(size:Int, passwordType: PasswordType): String {
        var password = ""

        if(size<0) return password

        val allowedDictionaryRange = passwordType.passwordRange()
        val passwordUpdateMutex = Mutex()
        repeatParallel(times = size){
            val symbol = dictionary[randomCore.generateNumberInRange(
                from = allowedDictionaryRange.first,
                to = allowedDictionaryRange.last)
            ]
            passwordUpdateMutex.withLock { password+=symbol }
        }

        return password
    }

}