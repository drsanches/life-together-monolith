package ru.drsanches.life_together.friends

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import spock.lang.Specification

class TestRemoveFriendRequest extends Specification {

    String PATH = "/friends/manage/"

    def "success current user frend request deletion"() {
        given: "two users and one side friend request"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        def token1 = RequestUtils.getToken(username1, password1)
        RequestUtils.sendFriendRequest(username1, password1, username2)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().delete(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()

        and: "the second user has correct relationships"
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
    }

    def "success another user friend request deletion"() {
        given: "two users and one side friend request"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        def token1 = RequestUtils.getToken(username1, password1)
        RequestUtils.sendFriendRequest(username2, password2, username1)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().delete(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()

        and: "the second user has correct relationships"
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
    }

    def "success friend deletion"() {
        given: "two friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        def token1 = RequestUtils.getToken(username1, password1)
        RequestUtils.sendFriendRequest(username1, password1, username2)
        RequestUtils.sendFriendRequest(username2, password2, username1)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().delete(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()

        and: "the second user has correct relationships"
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
    }

    def "delete nonexistent request"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getRestClient().delete(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()

        and: "the second user has correct relationships"
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
    }

    def "delete request for nonexistent user"() {
        given: "user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        def token1 = RequestUtils.getToken(username1, password1)
        def nonexistentUsername = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().delete(
                path: PATH + nonexistentUsername,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "delete current user request"() {
        given: "user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        def token1 = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().delete(
                path: PATH + username1,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }

    def "delete request with invalid token"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        RequestUtils.sendFriendRequest(username1, password1, username2)
        def token1 = UUID.randomUUID().toString()

        when: "sendRequest is called with invalid token"
        RequestUtils.getRestClient().delete(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()
        JSONArray outgoingRequests = RequestUtils.getOutgoingRequests(username1, password1)
        assert outgoingRequests.size() == 1
        assert outgoingRequests.get(0)["id"] != null
        assert outgoingRequests.get(0)["id"] != JSONNull.getInstance()
        assert outgoingRequests.get(0)["username"] == username2

        and: "the second user has correct relationships"
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
        JSONArray incomingRequests = RequestUtils.getIncomingRequests(username2, password2)
        assert incomingRequests.size() == 1
        assert incomingRequests.get(0)["id"] != null
        assert incomingRequests.get(0)["id"] != JSONNull.getInstance()
        assert incomingRequests.get(0)["username"] == username1
    }
}