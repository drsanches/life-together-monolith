package ru.drsanches.life_together.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import spock.lang.Specification

class TestRegistration extends Specification {

    String PATH = "/auth/registration"

    def "success user registration"() {
        given: "username and password"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().post(
                path: PATH,
                body:  [username: username,
                        password: password,
                        email: email],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 201
        assert response.getData()['id'] != null
        assert response.getData()['id'] != JSONNull.getInstance()
        assert response.getData()['username'] == username
        assert response.getData()['email'] == email

        and: "correct user was created"
        assert response.getData() == RequestUtils.getAuthInfo(username, password)

        and: "user profile was created"
        def userProfile = RequestUtils.getUserProfile(username, password)
        assert userProfile['id'] == response.getData()['id']
        assert userProfile['username'] == username
        assert userProfile['firstName'] == JSONNull.getInstance()
        assert userProfile['lastName'] == JSONNull.getInstance()
    }

    def "already existing user registration"() {
        given: "registered user"
        def username = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password1, null)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body:  [username: username,
                        password: password2],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }
}