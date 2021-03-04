package ru.drsanches.life_together.profile

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import spock.lang.Specification

class TestGetCurrentProfile extends Specification {

    String PATH = "/profile"

    def "successful current user profile getting"() {
        given: "user with token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def userId = RequestUtils.registerUser(username, password, null)
        def firstName = DataGenerator.createValidFirstName()
        def lastName = DataGenerator.createValidLastName()
        def token = RequestUtils.getToken(username, password)
        RequestUtils.changeUserProfile(token, firstName, lastName)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == userId
        assert response.getData()["username"] == username
        assert response.getData()["firstName"] == firstName
        assert response.getData()["lastName"] == lastName
    }

    def "get current user profile with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}