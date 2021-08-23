package ru.drsanches.life_together.debts

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import ru.drsanches.life_together.app.data.debts.enumeration.TransactionType
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import ru.drsanches.life_together.utils.Utils
import spock.lang.Specification

class TestSendMoney extends Specification {

    String PATH = "/api/v1/debts/send"

    def "success money send to one user"() {
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
        def message = DataGenerator.createValidMessage()
        def money = 100

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIds: [userId2],
                        money: money,
                        message: message],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "debts are correct"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["sent"] == JSONArray.fromObject(['userId': userId2, 'amount': money])
        assert debts1["received"] == new JSONArray()
        assert debts2["sent"] == new JSONArray()
        assert debts2["received"] == JSONArray.fromObject(['userId': userId1, 'amount': money])

        and: "history is correct"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 1
        assert history2.size() == 1
        def transaction1 = Utils.findTransaction(history1, userId2, TransactionType.OUTGOING, money, message)
        def transaction2 = Utils.findTransaction(history2, userId1, TransactionType.INCOMING, money, message)
        assert transaction1 != null
        assert transaction2 != null
        assert Utils.checkTimestamp(dateBefore, transaction1["timestamp"] as String, dateAfter)
        assert Utils.checkTimestamp(dateBefore, transaction2["timestamp"] as String, dateAfter)
    }

    def "success money send to both users"() {
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
        def message = DataGenerator.createValidMessage()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIds: [userId1, userId2],
                        money: money,
                        message: message],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "debts are correct"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["sent"] == JSONArray.fromObject(['userId': userId2, 'amount': money / 2 as int])
        assert debts1["received"] == new JSONArray()
        assert debts2["sent"] == new JSONArray()
        assert debts2["received"] == JSONArray.fromObject(['userId': userId1, 'amount': money / 2 as int])

        and: "history is correct"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 1
        assert history2.size() == 1
        def transaction1 = Utils.findTransaction(history1, userId2, TransactionType.OUTGOING, money / 2 as int, message)
        def transaction2 = Utils.findTransaction(history2, userId1, TransactionType.INCOMING, money / 2 as int, message)
        assert transaction1 != null
        assert transaction2 != null
        assert Utils.checkTimestamp(dateBefore, transaction1["timestamp"] as String, dateAfter)
        assert Utils.checkTimestamp(dateBefore, transaction2["timestamp"] as String, dateAfter)
    }

    def "success money send to two equal users"() {
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
        def message = DataGenerator.createValidMessage()
        def money = 100

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIds: [userId2, userId2],
                        money: money,
                        message: message],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "debts are correct"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["sent"] == JSONArray.fromObject(['userId': userId2, 'amount': money])
        assert debts1["received"] == new JSONArray()
        assert debts2["sent"] == new JSONArray()
        assert debts2["received"] == JSONArray.fromObject(['userId': userId1, 'amount': money])

        and: "history is correct"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 1
        assert history2.size() == 1
        def transaction1 = Utils.findTransaction(history1, userId2, TransactionType.OUTGOING, money, message)
        def transaction2 = Utils.findTransaction(history2, userId1, TransactionType.INCOMING, money, message)
        assert transaction1 != null
        assert transaction2 != null
        assert Utils.checkTimestamp(dateBefore, transaction1["timestamp"] as String, dateAfter)
        assert Utils.checkTimestamp(dateBefore, transaction2["timestamp"] as String, dateAfter)
    }

    def "invalid money send"() {
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

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIds: [userId2],
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        def e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "debts are empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["sent"] == new JSONArray()
        assert debts1["received"] == new JSONArray()
        assert debts2["sent"] == new JSONArray()
        assert debts2["received"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 0
        assert history2.size() == 0

        where:
        money << [null, 0, -1]
    }

    def "invalid user id list money send"() {
        given: "user and invalid user id list"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)

        def token1 = RequestUtils.getToken(username1, password1)
        def money = 100

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIds: toUserIds,
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "debts are empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        assert debts1["sent"] == new JSONArray()
        assert debts1["received"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        assert history1.size() == 0

        where:
        toUserIds << [null, [], [UUID.randomUUID().toString()]]
    }

    def "without friend requests money send"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        def token1 = RequestUtils.getToken(username1, password1)
        def money = 100

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIds: [userId2],
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "debts are empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["sent"] == new JSONArray()
        assert debts1["received"] == new JSONArray()
        assert debts2["sent"] == new JSONArray()
        assert debts2["received"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 0
        assert history2.size() == 0
    }

    def "with outgoing friend request money send"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)

        def token1 = RequestUtils.getToken(username1, password1)
        def money = 100

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIds: [userId2],
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "debts are empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["sent"] == new JSONArray()
        assert debts1["received"] == new JSONArray()
        assert debts2["sent"] == new JSONArray()
        assert debts2["received"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 0
        assert history2.size() == 0
    }

    def "with incoming friend request money send"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username2, password2, userId1)

        def token1 = RequestUtils.getToken(username1, password1)
        def money = 100

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIds: [userId2],
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "debts are empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["sent"] == new JSONArray()
        assert debts1["received"] == new JSONArray()
        assert debts2["sent"] == new JSONArray()
        assert debts2["received"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 0
        assert history2.size() == 0
    }

    def "send money to deleted friend"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)
        RequestUtils.sendFriendRequest(username2, password2, userId1)

        RequestUtils.deleteUser(username2, password2)

        def token1 = RequestUtils.getToken(username1, password1)
        def money = 100

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIds: [userId2],
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "debts are empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        assert debts1["sent"] == new JSONArray()
        assert debts1["received"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        assert history1.size() == 0
    }

    def "invalid token money send"() {
        given: "user with invalid token"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()

        def toUserId = RequestUtils.registerUser(username1, password1, null)

        def token = UUID.randomUUID().toString()
        def money = 100

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [toUserIdList: [toUserId],
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401

        and: "debts are empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        assert debts1["sent"] == new JSONArray()
        assert debts1["received"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        assert history1.size() == 0
    }
}