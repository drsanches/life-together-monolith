package ru.drsanches.life_together.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import spock.lang.Specification

class TestDeleteUser extends Specification {

    String PATH = "/auth/deleteUser"

    def "success user deleting"() {
        given: "registered user, password and token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200

        and: "user was deleted"
        assert RequestUtils.getAuthInfo(token) == null
        assert RequestUtils.getToken(username, password) == null

        and: "new user with old user credentials has different token"
        RequestUtils.registerUser(username, password, null)
        assert RequestUtils.getAuthInfo(username, password) != null
        assert RequestUtils.getToken(username, password) != token
    }

    def "user deleting with invalid password"() {
        given: "registered user, token and invalid password"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)
        def invalidPassword = DataGenerator.createValidPassword()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [password: invalidPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "user was not deleted"
        assert RequestUtils.getAuthInfo(username, password) != null
    }

    def "disable user with invalid token"() {
        given: "registered user, password and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401

        and: "user was not deleted"
        assert RequestUtils.getAuthInfo(username, password) != null
    }
}