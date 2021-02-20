package ru.drsanches.life_together.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import spock.lang.Specification

class TestChangeEmail extends Specification {

    String PATH = "/auth/changeEmail"

    def "success email change"() {
        given: "registered user, password, token and new email"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        RequestUtils.registerUser(username, password, email)
        def token = RequestUtils.getToken(username, password)
        def newEmail = DataGenerator.createValidEmail()

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newEmail: newEmail,
                        password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200

        and: "user was updated"
        assert RequestUtils.getAuthInfo(username, password)['email'] == newEmail
    }

    def "email change with old email"() {
        given: "registered user, password, token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        RequestUtils.registerUser(username, password, email)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newEmail: email,
                        password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "email change with invalid password"() {
        given: "registered user, new email, token and invalid password"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        def newEmail = DataGenerator.createValidEmail()
        def invalidPassword = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, email)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newEmail: newEmail,
                        password: invalidPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "email change with invalid token"() {
        given: "registered user, new email and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        RequestUtils.registerUser(username, password,  email)
        def token = UUID.randomUUID().toString()
        def newEmail = DataGenerator.createValidEmail()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newEmail: newEmail,
                        password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}