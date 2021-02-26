package ru.drsanches.life_together.profile

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import net.sf.json.JSONObject
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import spock.lang.Specification

class TestChangeCurrentProfile extends Specification {

    String PATH = "/profile"

    def "success user profile change"() {
        given: "user, token and new profile data"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)
        def firstName = DataGenerator.createValidFirstName()
        def lastName = DataGenerator.createValidLastName()

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [firstName: firstName,
                        lastName: lastName],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200

        and: "user profile was updated"
        JSONObject userProfile = RequestUtils.getUserProfile(username, password)
        userProfile['id'] != null
        userProfile['id'] != JSONNull.getInstance()
        userProfile['username'] == username
        userProfile['firstName'] == firstName
        userProfile['lastName'] == lastName
    }

    def "user profile change with invalid token"() {
        given: "user, new profile data and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password,  null)
        def token = UUID.randomUUID().toString()
        def firstName = DataGenerator.createValidFirstName()
        def lastName = DataGenerator.createValidLastName()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [firstName: firstName,
                        lastName: lastName],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}