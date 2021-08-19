package ru.drsanches.life_together.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import spock.lang.Specification

class TestRefreshToken extends Specification {

    String PATH = "/api/v1/auth/refreshToken"

    def "successful token refresh"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def refreshToken = RequestUtils.getRefreshToken(username, password)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $refreshToken"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "token is correct"
        def token = response.getData()["accessToken"]
        assert RequestUtils.getAuthInfo(token as String) != null
    }

    def "refresh token with invalid refreshToken"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        RequestUtils.getToken(username, password)
        def invalidRefreshToken = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $invalidRefreshToken"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}