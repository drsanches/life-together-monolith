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

class TestCancel extends Specification {

    String PATH = "/api/v1/debts/cancel/"

    String messageFormat = "Debt has been canceled by user with id '%s'"

    def "success cancel friend outgoing debt"() {
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

        def money = 100
        RequestUtils.sendMoney(token1, [userId2] as String[], money, null)

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().get(
                path: PATH + userId2,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new system transaction"
        def transactions = RequestUtils.getHistory(username1, password1)
        assert transactions.size() == 2
        def transaction = Utils.findTransaction(transactions, userId2, TransactionType.SYSTEM, money, String.format(messageFormat, userId1))
        assert transaction != null
        assert Utils.checkTimestamp(dateBefore, transaction["timestamp"] as String, dateAfter)
    }

    def "success cancel friend incoming debt"() {
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
        def token2 = RequestUtils.getToken(username2, password2)

        def money = 100
        RequestUtils.sendMoney(token2, [userId1] as String[], money, null)

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().get(
                path: PATH + userId2,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new system transaction"
        def transactions = RequestUtils.getHistory(username1, password1)
        assert transactions.size() == 2
        def transaction = Utils.findTransaction(transactions, userId2, TransactionType.SYSTEM, money, String.format(messageFormat, userId1))
        assert transaction != null
        assert Utils.checkTimestamp(dateBefore, transaction["timestamp"] as String, dateAfter)
    }

    def "success cancel other user outgoing debt"() {
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

        def money = 100
        RequestUtils.sendMoney(token1, [userId2] as String[], money, null)
        RequestUtils.deleteFriendRequest(username1, password1, userId2)

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().get(
                path: PATH + userId2,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new system transaction"
        def transactions = RequestUtils.getHistory(username1, password1)
        assert transactions.size() == 2
        def transaction = Utils.findTransaction(transactions, userId2, TransactionType.SYSTEM, money, String.format(messageFormat, userId1))
        assert transaction != null
        assert Utils.checkTimestamp(dateBefore, transaction["timestamp"] as String, dateAfter)
    }

    def "success cancel other user incoming debt"() {
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
        RequestUtils.sendMoney(token2, [userId1] as String[], money, null)
        RequestUtils.deleteFriendRequest(username1, password1, userId2)

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().get(
                path: PATH + userId2,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new system transaction"
        def transactions = RequestUtils.getHistory(username1, password1)
        assert transactions.size() == 2
        def transaction = Utils.findTransaction(transactions, userId2, TransactionType.SYSTEM, money, String.format(messageFormat, userId1))
        assert transaction != null
        assert Utils.checkTimestamp(dateBefore, transaction["timestamp"] as String, dateAfter)
    }

    def "success cancel deleted user outgoing debt"() {
        given: "user and deleted user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)
        RequestUtils.sendFriendRequest(username2, password2, userId1)

        def token1 = RequestUtils.getToken(username1, password1)

        def money = 100
        RequestUtils.sendMoney(token1, [userId2] as String[], money, null)
        RequestUtils.deleteUser(username2, password2)

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().get(
                path: PATH + userId2,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new system transaction"
        def transactions = RequestUtils.getHistory(username1, password1)
        assert transactions.size() == 2
        def transaction = Utils.findTransaction(transactions, userId2, TransactionType.SYSTEM, money, String.format(messageFormat, userId1))
        assert transaction != null
        assert Utils.checkTimestamp(dateBefore, transaction["timestamp"] as String, dateAfter)
    }

    def "success cancel deleted user incoming debt"() {
        given: "user and deleted user"
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
        RequestUtils.sendMoney(token2, [userId1] as String[], money, null)
        RequestUtils.deleteUser(username2, password2)

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().get(
                path: PATH + userId2,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new system transaction"
        def transactions = RequestUtils.getHistory(username1, password1)
        assert transactions.size() == 2
        def transaction = Utils.findTransaction(transactions, userId2, TransactionType.SYSTEM, money, String.format(messageFormat, userId1))
        assert transaction != null
        assert Utils.checkTimestamp(dateBefore, transaction["timestamp"] as String, dateAfter)
    }

    def "cancel empty user debt"() {
        given: "user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + userId2,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        def e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "cancel nonexistent user debt"() {
        given: "user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        def userId2 = UUID.randomUUID().toString()
        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + userId2,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        def e = thrown(HttpResponseException)
        assert e.response.status == 404
    }

    def "cancel debt to yourself"() {
        given: "user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + userId1,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        def e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "invalid token debt cancel"() {
        given: "user with invalid token"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        def token1 = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + userId2,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        def e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}