package ru.drsanches.life_together.debts

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import ru.drsanches.life_together.utils.Utils
import spock.lang.Specification

class TestGetDebts extends Specification {

    String PATH = "/api/v1/debts"

    def "success sent debts getting"() {
        given: "user with friend, other user, users with incoming and outgoing requests and deleted user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def friendUsername = DataGenerator.createValidUsername()
        def friendPassword = DataGenerator.createValidPassword()
        def otherUsername = DataGenerator.createValidUsername()
        def otherPassword = DataGenerator.createValidPassword()
        def incomingUsername = DataGenerator.createValidUsername()
        def incomingPassword = DataGenerator.createValidPassword()
        def outgoingUsername = DataGenerator.createValidUsername()
        def outgoingPassword = DataGenerator.createValidPassword()
        def deletedUsername = DataGenerator.createValidUsername()
        def deletedPassword = DataGenerator.createValidPassword()

        def userId = RequestUtils.registerUser(username, password, null)
        def friendId = RequestUtils.registerUser(friendUsername, friendPassword, null)
        def otherId = RequestUtils.registerUser(otherUsername, otherPassword, null)
        def incomingId = RequestUtils.registerUser(incomingUsername, incomingPassword, null)
        def outgoingId = RequestUtils.registerUser(outgoingUsername, outgoingPassword, null)
        def deletedId = RequestUtils.registerUser(deletedUsername, deletedPassword, null)

        RequestUtils.sendFriendRequest(username, password, friendId)
        RequestUtils.sendFriendRequest(friendUsername, friendPassword, userId)
        RequestUtils.sendFriendRequest(username, password, otherId)
        RequestUtils.sendFriendRequest(otherUsername, otherPassword, userId)
        RequestUtils.sendFriendRequest(username, password, incomingId)
        RequestUtils.sendFriendRequest(incomingUsername, incomingPassword, userId)
        RequestUtils.sendFriendRequest(username, password, outgoingId)
        RequestUtils.sendFriendRequest(outgoingUsername, outgoingPassword, userId)
        RequestUtils.sendFriendRequest(username, password, deletedId)
        RequestUtils.sendFriendRequest(deletedUsername, deletedPassword, userId)

        def token = RequestUtils.getToken(username, password)
        def money = 600
        RequestUtils.sendMoney(token, [userId, friendId, otherId, incomingId, outgoingId, deletedId] as String[], money, null)

        RequestUtils.deleteFriendRequest(username, password, otherId)
        RequestUtils.deleteFriendRequest(username, password, incomingId)
        RequestUtils.sendFriendRequest(incomingUsername, incomingPassword, userId)
        RequestUtils.deleteFriendRequest(username, password, outgoingId)
        RequestUtils.sendFriendRequest(username, password, outgoingId)
        RequestUtils.deleteUser(deletedUsername, deletedPassword)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "debts are correct"
        assert response.getData()["received"] == new JSONArray()
        assert (response.getData()["sent"] as JSONArray).size() == 5
        assert Utils.getAmount(response.getData()["sent"] as JSONArray, friendId) == money / 6
        assert Utils.getAmount(response.getData()["sent"] as JSONArray, otherId) == money / 6
        assert Utils.getAmount(response.getData()["sent"] as JSONArray, incomingId) == money / 6
        assert Utils.getAmount(response.getData()["sent"] as JSONArray, outgoingId) == money / 6
        assert Utils.getAmount(response.getData()["sent"] as JSONArray, deletedId) == money / 6
    }

    def "success received debts getting"() {
        given: "user with friend, other user, users with incoming and outgoing requests and deleted user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def friendUsername = DataGenerator.createValidUsername()
        def friendPassword = DataGenerator.createValidPassword()
        def otherUsername = DataGenerator.createValidUsername()
        def otherPassword = DataGenerator.createValidPassword()
        def incomingUsername = DataGenerator.createValidUsername()
        def incomingPassword = DataGenerator.createValidPassword()
        def outgoingUsername = DataGenerator.createValidUsername()
        def outgoingPassword = DataGenerator.createValidPassword()
        def deletedUsername = DataGenerator.createValidUsername()
        def deletedPassword = DataGenerator.createValidPassword()

        def userId = RequestUtils.registerUser(username, password, null)
        def friendId = RequestUtils.registerUser(friendUsername, friendPassword, null)
        def otherId = RequestUtils.registerUser(otherUsername, otherPassword, null)
        def incomingId = RequestUtils.registerUser(incomingUsername, incomingPassword, null)
        def outgoingId = RequestUtils.registerUser(outgoingUsername, outgoingPassword, null)
        def deletedId = RequestUtils.registerUser(deletedUsername, deletedPassword, null)

        RequestUtils.sendFriendRequest(username, password, friendId)
        RequestUtils.sendFriendRequest(friendUsername, friendPassword, userId)
        RequestUtils.sendFriendRequest(username, password, otherId)
        RequestUtils.sendFriendRequest(otherUsername, otherPassword, userId)
        RequestUtils.sendFriendRequest(username, password, incomingId)
        RequestUtils.sendFriendRequest(incomingUsername, incomingPassword, userId)
        RequestUtils.sendFriendRequest(username, password, outgoingId)
        RequestUtils.sendFriendRequest(outgoingUsername, outgoingPassword, userId)
        RequestUtils.sendFriendRequest(username, password, deletedId)
        RequestUtils.sendFriendRequest(deletedUsername, deletedPassword, userId)

        def token = RequestUtils.getToken(username, password)
        def friendToken = RequestUtils.getToken(friendUsername, friendPassword)
        def otherToken = RequestUtils.getToken(otherUsername, otherPassword)
        def incomingToken = RequestUtils.getToken(incomingUsername, incomingPassword)
        def outgoingToken = RequestUtils.getToken(outgoingUsername, outgoingPassword)
        def deletedToken = RequestUtils.getToken(deletedUsername, deletedPassword)
        def money = 100
        RequestUtils.sendMoney(friendToken, [userId] as String[], money, null)
        RequestUtils.sendMoney(otherToken, [userId] as String[], money, null)
        RequestUtils.sendMoney(incomingToken, [userId] as String[], money, null)
        RequestUtils.sendMoney(outgoingToken, [userId] as String[], money, null)
        RequestUtils.sendMoney(deletedToken, [userId] as String[], money, null)

        RequestUtils.deleteFriendRequest(username, password, otherId)
        RequestUtils.deleteFriendRequest(username, password, incomingId)
        RequestUtils.sendFriendRequest(incomingUsername, incomingPassword, userId)
        RequestUtils.deleteFriendRequest(username, password, outgoingId)
        RequestUtils.sendFriendRequest(username, password, outgoingId)
        RequestUtils.deleteUser(deletedUsername, deletedPassword)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "debts are correct"
        assert response.getData()["sent"] == new JSONArray()
        assert (response.getData()["received"] as JSONArray).size() == 5
        assert Utils.getAmount(response.getData()["received"] as JSONArray, friendId) == money
        assert Utils.getAmount(response.getData()["received"] as JSONArray, otherId) == money
        assert Utils.getAmount(response.getData()["received"] as JSONArray, incomingId) == money
        assert Utils.getAmount(response.getData()["received"] as JSONArray, outgoingId) == money
        assert Utils.getAmount(response.getData()["received"] as JSONArray, deletedId) == money
    }

    def "success empty debts getting"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)
        RequestUtils.sendFriendRequest(username2, password2, userId1)

        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)

        def money = 100
        RequestUtils.sendMoney(token1, [userId2] as String[], money, null)
        RequestUtils.sendMoney(token2, [userId1] as String[], money, null)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "debts are correct"
        assert response.getData()["sent"] == new JSONArray()
        assert response.getData()["received"] == new JSONArray()
    }

    def "invalid token debts getting"() {
        given: "user with invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        def e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}