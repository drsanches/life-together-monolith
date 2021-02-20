package ru.drsanches.life_together.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONObject
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

        when: "registration is called"
        HttpResponseDecorator response = RequestUtils.getRestClient().post(
                path: PATH,
                body:  [username: username,
                        password: password,
                        email: email],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 201

        and: "correct user was created"
        JSONObject authInfo = RequestUtils.getAuthInfo(username, password)
        assert authInfo['username'] == username
        assert authInfo['email'] == email
    }

    def "already existing user registration"() {
        given: "registered user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)

        when: "registration is called"
        RequestUtils.getRestClient().post(
                path: PATH,
                body:  [username: username,
                        password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }
}