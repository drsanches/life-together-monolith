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

    String PATH = "/debts/history"

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