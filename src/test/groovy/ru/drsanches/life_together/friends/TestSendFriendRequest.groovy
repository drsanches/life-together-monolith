package ru.drsanches.life_together.friends

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import spock.lang.Specification

class TestSendFriendRequest extends Specification {

    String PATH = "/friends/manage/"

    def "success one side friend request sending"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def firstName1 = DataGenerator.createValidFirstName()
        def lastName1 = DataGenerator.createValidLastName()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def firstName2 = DataGenerator.createValidFirstName()
        def lastName2 = DataGenerator.createValidLastName()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)
        RequestUtils.changeUserProfile(token1, firstName1, lastName1)
        RequestUtils.changeUserProfile(token2, firstName2, lastName2)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().post(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 201

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()
        JSONArray outgoingRequests = RequestUtils.getOutgoingRequests(username1, password1)
        assert outgoingRequests.size() == 1
        assert outgoingRequests.get(0)["id"] != null
        assert outgoingRequests.get(0)["id"] != JSONNull.getInstance()
        assert outgoingRequests.get(0)["username"] == username2
        assert outgoingRequests.get(0)["firstName"] == firstName2
        assert outgoingRequests.get(0)["lastName"] == lastName2

        and: "the second user has correct relationships"
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
        JSONArray incomingRequests = RequestUtils.getIncomingRequests(username2, password2)
        assert incomingRequests.size() == 1
        assert incomingRequests.get(0)["id"] != null
        assert incomingRequests.get(0)["id"] != JSONNull.getInstance()
        assert incomingRequests.get(0)["username"] == username1
        assert incomingRequests.get(0)["firstName"] == firstName1
        assert incomingRequests.get(0)["lastName"] == lastName1
    }

    def "success two side friend request sending"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def firstName1 = DataGenerator.createValidFirstName()
        def lastName1 = DataGenerator.createValidLastName()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def firstName2 = DataGenerator.createValidFirstName()
        def lastName2 = DataGenerator.createValidLastName()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)
        RequestUtils.changeUserProfile(token1, firstName1, lastName1)
        RequestUtils.changeUserProfile(token2, firstName2, lastName2)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().post(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 201

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        JSONArray friends1 = RequestUtils.getFriends(username1, password1)
        assert friends1.size() == 1
        assert friends1.get(0)["id"] != null
        assert friends1.get(0)["id"] != JSONNull.getInstance()
        assert friends1.get(0)["username"] == username2
        assert friends1.get(0)["firstName"] == firstName2
        assert friends1.get(0)["lastName"] == lastName2

        and: "the second user has correct relationships"
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        JSONArray friends2 = RequestUtils.getFriends(username2, password2)
        assert friends2.size() == 1
        assert friends2.get(0)["id"] != null
        assert friends2.get(0)["id"] != JSONNull.getInstance()
        assert friends2.get(0)["username"] == username1
        assert friends2.get(0)["firstName"] == firstName1
        assert friends2.get(0)["lastName"] == lastName1
    }

    def "second time friend request sending"() {
        given: "two users with one side friend request"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def firstName1 = DataGenerator.createValidFirstName()
        def lastName1 = DataGenerator.createValidLastName()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def firstName2 = DataGenerator.createValidFirstName()
        def lastName2 = DataGenerator.createValidLastName()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        RequestUtils.sendFriendRequest(username1, password1, username2)
        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)
        RequestUtils.changeUserProfile(token1, firstName1, lastName1)
        RequestUtils.changeUserProfile(token2, firstName2, lastName2)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().post(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 201

        and: "the first user relationship has not changed"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()
        JSONArray outgoingRequests = RequestUtils.getOutgoingRequests(username1, password1)
        assert outgoingRequests.size() == 1
        assert outgoingRequests.get(0)["id"] != null
        assert outgoingRequests.get(0)["id"] != JSONNull.getInstance()
        assert outgoingRequests.get(0)["username"] == username2
        assert outgoingRequests.get(0)["firstName"] == firstName2
        assert outgoingRequests.get(0)["lastName"] == lastName2

        and: "the second user relationship has not changed"
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
        JSONArray incomingRequests = RequestUtils.getIncomingRequests(username2, password2)
        assert incomingRequests.size() == 1
        assert incomingRequests.get(0)["id"] != null
        assert incomingRequests.get(0)["id"] != JSONNull.getInstance()
        assert incomingRequests.get(0)["username"] == username1
        assert incomingRequests.get(0)["firstName"] == firstName1
        assert incomingRequests.get(0)["lastName"] == lastName1
    }

    def "send friend request to nonexistent user"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)
        def nonexistentUsername = DataGenerator.createValidUsername()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH + nonexistentUsername,
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "send friend request to current user"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH + username,
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "send friend request with invalid token"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        def token1 = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401

        and: "users has no relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
    }
}