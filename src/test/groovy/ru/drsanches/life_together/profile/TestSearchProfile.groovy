package ru.drsanches.life_together.profile

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import ru.drsanches.life_together.utils.Utils
import spock.lang.Specification

class TestSearchProfile extends Specification {

    String PATH = "/api/v1/profile/search/"

    def "successful user profile searching"() {
        given: "user with token and another user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)

        def firstName2 = DataGenerator.createValidFirstName()
        def lastName2 = DataGenerator.createValidLastName()
        RequestUtils.changeUserProfile(token2, firstName2, lastName2)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == userId2
        assert response.getData()["username"] == username2
        assert response.getData()["firstName"] == firstName2
        assert response.getData()["lastName"] == lastName2
        assert response.getData()["imagePath"] == Utils.getDefaultImagePath()
    }

    def "search deleted user profile"() {
        given: "user, token and deleted user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)

        def token = RequestUtils.getToken(username1, password1)

        RequestUtils.deleteUser(username2, password2)

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 404
    }

    def "search nonexistent user profile"() {
        given: "user, token and nonexistent id"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)
        def nonexistentUsername = DataGenerator.createValidUsername()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + nonexistentUsername,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 404
    }

    def "get user profile with invalid token"() {
        given: "user and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + username,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}