package ru.drsanches.life_together.utils

class DataGenerator {

    static String createValidUsername() {
        return UUID.randomUUID().toString()
    }

    static String createValidPassword() {
        return UUID.randomUUID().toString()
    }

    static String createValidEmail() {
        return UUID.randomUUID().toString()
    }
}