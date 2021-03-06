package ru.drsanches.life_together.profile

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONObject
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import ru.drsanches.life_together.utils.Utils
import spock.lang.Specification

class TestChangeCurrentProfile extends Specification {

    String PATH = "/api/v1/profile"

    def "success user profile change"() {
        given: "user, token and new profile data"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def userId = RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)
        def firstName = DataGenerator.createValidFirstName()
        def lastName = DataGenerator.createValidLastName()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [firstName: firstName,
                        lastName: lastName],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "user profile was updated"
        JSONObject userProfile = RequestUtils.getUserProfile(username, password)
        userProfile['id'] == userId
        userProfile['username'] == username
        userProfile['firstName'] == firstName
        userProfile['lastName'] == lastName
        userProfile['imagePath'] == Utils.getDefaultImagePath()
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