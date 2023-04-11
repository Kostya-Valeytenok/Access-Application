package com.rainc.random

import java.lang.NumberFormatException

internal class RandomCore(private val random: PRNGFixes.LinuxPRNGSecureRandom) {


    fun randomForDice(): Int {
        val diceDataValue = ByteArray(2)
        var number:Byte
        initializeAndFillSimpleDataArray(diceDataValue)
        number = diceDataValue[1]

        while (number.isValidateDiceValue().not()){
            random.engineNextBytes(diceDataValue)
            number = diceDataValue[1]
        }

        return number.module().moduloSix().toInt()
    }

    private fun Byte.isValidateDiceValue(): Boolean {
        val value = this.toInt()
        return (value == 0  || value == -128 || value == -127 || value == 127).not()
    }

    private fun initializeAndFillSimpleDataArray(array: ByteArray) {
        array[0] = random.engineGenerateSeed(1)[0]
        random.engineNextBytes(array)
    }

    private fun ByteArray.init(){
        set(0,random.engineGenerateSeed(1)[0])
        random.engineNextBytes(this)
    }

    fun generateNumberInRange(from:Int, to:Int): Int {
        if(to<from) throw NumberFormatException()
        val range = from..to
        val numSize = getNumberSize(value = to)
        var value = numberGeneration(numSize = numSize)

        while (range.contains(value).not()){
            value = numberGeneration(numSize = numSize)
        }

        return value
    }

    fun numberGeneration(numSize: Int): Int {
        val data = ByteArray(numSize)
        var number = 0
        initializeAndFillSimpleDataArray(data)

        data.validate()

        var mn = 1
        for (i in 0 until numSize - 1) mn *= 10
        for (i in 0 until numSize) {
            number += data[i] * mn
            mn /= 10
        }
        return number
    }

    private fun ByteArray.validate(){
        var i = 0
        while (i < size) {
            val value = get(i)
            when{
                value.isNotValidNGGenValue() -> set(i,random.engineGenerateSeed(1)[0].module())
                value < 0 -> set(i,value.module())
                value >= 10 -> {
                    set(i, value.moduloTen())
                    i++
                }
                else ->{
                    set(i,value)
                    i++
                }
            }
        }
    }

    fun coinRandomValue(): Int {
        val coinData = ByteArray(2)
        coinData.init()
        coinData[1] = coinData[1].module().moduloTwo()
        return coinData[1].toInt()
    }

    private fun Byte.module(): Byte {
        return if(this < 0) return negative() else this
    }

    private fun Byte.moduloTen(): Byte {
        return (this % 10).toByte()
    }

    private fun Byte.moduloTwo(): Byte {
        return (this % 2).toByte()
    }

    private fun Byte.moduloSix(): Byte {
        return (this % 6).toByte()
    }

    private fun Byte.negative(): Byte {
        return (this.toInt() * -1).toByte()
    }

    private fun Byte.isNotValidNGGenValue(): Boolean {
        return this > 120 || this < -120 || this.toInt() == 0
    }

    private fun getNumberSize(value: Int): Int {
        var sizeNum = 1
        var sizeX = value.toDouble()
        if (sizeX > 9) while (sizeX > 9) {
            sizeX /= 10.0
            sizeNum++
        }
        return sizeNum
    }
}