package ru.drsanches.life_together.friends

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import spock.lang.Specification

class TestGetOutgoingRequests extends Specification {

    String PATH = "/friends/requests/outgoing"

    def "success outgoing requests getting"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def firstName2 = DataGenerator.createValidFirstName()
        def lastName2 = DataGenerator.createValidLastName()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        RequestUtils.sendFriendRequest(username1, password1, username2)
        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)
        RequestUtils.changeUserProfile(token2, firstName2, lastName2)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 200
        JSONArray body = ((JSONArray) response.getData())
        assert body.size() == 1
        assert body.get(0)["id"] != null
        assert body.get(0)["id"] != JSONNull.getInstance()
        assert body.get(0)["username"] == username2
        assert body.get(0)["firstName"] == firstName2
        assert body.get(0)["lastName"] == lastName2
    }

    def "success empty outgoing requests getting"() {
        given: "three users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def username3 = DataGenerator.createValidUsername()
        def password3 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        RequestUtils.registerUser(username3, password3, null)
        RequestUtils.sendFriendRequest(username1, password1, username2)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        RequestUtils.sendFriendRequest(username3, password3, username1)
        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 200
        JSONArray body = ((JSONArray) response.getData())
        assert body.size() == 0
    }

    def "get outgoing requests with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}