package ru.drsanches.life_together.user

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import spock.lang.Specification

class TestGetUser extends Specification {

    String PATH = "/user"

    def "successful user profile getting"() {
        given: "user with token and another user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username2, password2, null)
        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)
        def firstName1 = DataGenerator.createValidFirstName()
        def lastName1 = DataGenerator.createValidLastName()
        RequestUtils.changeUserProfile(token1, firstName1, lastName1)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().get(
                path: PATH + "/$username1",
                headers: ["Authorization": "Bearer $token2"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] != null
        assert response.getData()["id"] != JSONNull.getInstance()
        assert response.getData()["username"] == username1
        assert response.getData()["firstName"] == firstName1
        assert response.getData()["lastName"] == lastName1
    }

    def "get nonexistent user profile"() {
        given: "user, token and nonexistent username"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)
        def nonexistentUsername = DataGenerator.createValidUsername()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + "/$nonexistentUsername",
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "get user profile with invalid token"() {
        given: "user and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + "/$username",
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}