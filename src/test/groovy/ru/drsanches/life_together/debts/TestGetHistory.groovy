package ru.drsanches.life_together.debts

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import ru.drsanches.life_together.data.debts.enumeration.TransactionType
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import ru.drsanches.life_together.utils.Utils
import spock.lang.Specification

class TestGetHistory extends Specification {

    String PATH = "/api/v1/debts/history"

    def "success history getting"() {
        given: "two users and deleted user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def otherUsername = DataGenerator.createValidUsername()
        def otherPassword = DataGenerator.createValidPassword()
        def deletedUsername = DataGenerator.createValidUsername()
        def deletedPassword = DataGenerator.createValidPassword()

        def userId = RequestUtils.registerUser(username, password, null)
        def otherId = RequestUtils.registerUser(otherUsername, otherPassword, null)
        def deletedId = RequestUtils.registerUser(deletedUsername, deletedPassword, null)

        RequestUtils.sendFriendRequest(username, password, otherId)
        RequestUtils.sendFriendRequest(otherUsername, otherPassword, userId)
        RequestUtils.sendFriendRequest(username, password, deletedId)
        RequestUtils.sendFriendRequest(deletedUsername, deletedPassword, userId)

        def token = RequestUtils.getToken(username, password)
        def otherToken = RequestUtils.getToken(otherUsername, otherPassword)
        def deletedToken = RequestUtils.getToken(deletedUsername, deletedPassword)

        def message1 = DataGenerator.createValidMessage()
        def message2 = DataGenerator.createValidMessage()
        def message3 = DataGenerator.createValidMessage()

        def money = 100
        def period1 = RequestUtils.sendMoney(token, [userId, otherId, deletedId] as String[], money * 3, message1)
        def period2 = RequestUtils.sendMoney(otherToken, [userId, otherId] as String[], money * 2, message2)
        def period3 = RequestUtils.sendMoney(deletedToken, [userId] as String[], money * 2, message3)

        RequestUtils.deleteFriendRequest(username, password, otherId)
        RequestUtils.deleteUser(deletedUsername, deletedPassword)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def transactions = response.getData() as JSONArray
        assert transactions.size() == 4
        def transaction1 = Utils.findTransaction(transactions, otherId, TransactionType.OUTGOING, money, message1)
        def transaction2 = Utils.findTransaction(transactions, deletedId, TransactionType.OUTGOING, money, message1)
        def transaction3 = Utils.findTransaction(transactions, otherId, TransactionType.INCOMING, money, message2)
        def transaction4 = Utils.findTransaction(transactions, deletedId, TransactionType.INCOMING, money * 2, message3)
        assert transaction1 != null
        assert transaction2 != null
        assert transaction3 != null
        assert transaction4 != null
        assert Utils.checkTimestamp(period1[0], transaction1["timestamp"] as String, period1[1])
        assert Utils.checkTimestamp(period1[0], transaction2["timestamp"] as String, period1[1])
        assert Utils.checkTimestamp(period2[0], transaction3["timestamp"] as String, period2[1])
        assert Utils.checkTimestamp(period3[0], transaction4["timestamp"] as String, period3[1])
    }

    // TODO: group with "success history getting with pagination"()
    // Strange Spock "where" limit on 12, may be fixed later
    def "success history getting with order check"() {
        given: "two friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)
        RequestUtils.sendFriendRequest(username2, password2, userId1)

        def token1 = RequestUtils.getToken(username1, password1)

        for (int i = 0; i < 10; i++) {
            RequestUtils.sendMoney(token1, [userId2] as String[], 100, i as String)
        }

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def transactions = response.getData() as JSONArray
        assert transactions.stream()
                .map({ x -> x["message"] })
                .reduce({ x1, x2 -> x1 + x2 })
                .orElse("") == "9876543210"
    }

    def "success history getting with pagination"() {
        given: "two friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)
        RequestUtils.sendFriendRequest(username2, password2, userId1)

        def token1 = RequestUtils.getToken(username1, password1)

        for (int i = 0; i < NUM; i++) {
            RequestUtils.sendMoney(token1, [userId2] as String[], 100, i as String)
        }

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                query: params,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def transactions = response.getData() as JSONArray
        assert transactions.stream()
                .map({ x -> x["message"] })
                .reduce({ x1, x2 -> x1 + x2 })
                .orElse("") == messages

        where:
        NUM | params          || messages
        // 10  | []              || "9876543210"
        10  | [from:2,to:5]   || "765"
        10  | [from:0,to:11]  || "9876543210"
        10  | [from:-1,to:-1] || "9876543210"
        10  | [from:5,to:-1]  || "43210"
        10  | [from:-1,to:5]  || "98765"
        10  | [from:20]       || ""
        10  | [from:5]        || "43210"
        10  | [from:-1]       || "9876543210"
        10  | [to:20]         || "9876543210"
        10  | [to:5]          || "98765"
        10  | [to:-1]         || "9876543210"
    }

    def "success empty history getting"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData() == new JSONArray()
    }

    def "invalid token history getting"() {
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