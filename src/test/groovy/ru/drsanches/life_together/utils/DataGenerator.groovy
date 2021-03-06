package ru.drsanches.life_together.utils

class DataGenerator {

    static String createValidUsername() {
        return "username-" + UUID.randomUUID().toString()
    }

    static String createValidPassword() {
        return "password-" + UUID.randomUUID().toString()
    }

    static String createValidEmail() {
        return "email-" + UUID.randomUUID().toString()
    }

    static String createValidFirstName() {
        return "firstName-" + UUID.randomUUID().toString()
    }

    static String createValidLastName() {
        return "lastName-" + UUID.randomUUID().toString()
    }

    static String createValidMessage() {
        return "message-" + UUID.randomUUID().toString()
    }
}