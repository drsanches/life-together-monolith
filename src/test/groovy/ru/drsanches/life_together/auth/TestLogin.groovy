package ru.drsanches.life_together.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import spock.lang.Specification

class TestLogin extends Specification {

    String PATH = "/auth/login"

    def "successful login"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().post(
                path: PATH,
                body: ["username": username,
                       "password": password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200

        and: "token is correct"
        def token = response.getData()["accessToken"]
        assert RequestUtils.getAuthInfo(token as String) != null
    }

    def "login with invalid password"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def invalidPassword = DataGenerator.createValidPassword()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body: ["username": username,
                       "password": invalidPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}