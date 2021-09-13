package ru.drsanches.life_together.debts

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import ru.drsanches.life_together.app.data.debts.dto.TransactionDTOType
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
                body: [transactions: [[toUserId: userId2,
                                       money: money,
                                       message: message]]],
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
        def transaction1 = Utils.findTransaction(history1, userId2, TransactionDTOType.OUTGOING, money, message)
        def transaction2 = Utils.findTransaction(history2, userId1, TransactionDTOType.INCOMING, money, message)
        assert transaction1 != null
        assert transaction2 != null
        assert Utils.checkTimestamp(dateBefore, transaction1["timestamp"] as String, dateAfter)
        assert Utils.checkTimestamp(dateBefore, transaction2["timestamp"] as String, dateAfter)
    }

    def "success money send to one user twice"() {
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
        def money1 = 100
        def money2 = 200
        def message1 = DataGenerator.createValidMessage()
        def message2 = DataGenerator.createValidMessage()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: [transactions: [[toUserId: userId2,
                                       money: money1,
                                       message: message1],
                                      [toUserId: userId2,
                                       money: money2,
                                       message: message2]]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "debts are correct"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["sent"] == JSONArray.fromObject(['userId': userId2, 'amount': money1 + money2])
        assert debts1["received"] == new JSONArray()
        assert debts2["sent"] == new JSONArray()
        assert debts2["received"] == JSONArray.fromObject(['userId': userId1, 'amount': money1 + money2])

        and: "current user history is correct"
        def history1 = RequestUtils.getHistory(username1, password1)
        assert history1.size() == 2
        def transaction11 = Utils.findTransaction(history1, userId2, TransactionDTOType.OUTGOING, money1, message1)
        def transaction12 = Utils.findTransaction(history1, userId2, TransactionDTOType.OUTGOING, money2, message2)
        assert transaction11 != null
        assert transaction12 != null
        assert Utils.checkTimestamp(dateBefore, transaction11["timestamp"] as String, dateAfter)
        assert Utils.checkTimestamp(dateBefore, transaction12["timestamp"] as String, dateAfter)

        and: "friend history is correct"
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history2.size() == 2
        def transaction21 = Utils.findTransaction(history2, userId1, TransactionDTOType.INCOMING, money1, message1)
        def transaction22 = Utils.findTransaction(history2, userId1, TransactionDTOType.INCOMING, money2, message2)
        assert transaction21 != null
        assert transaction22 != null
        assert Utils.checkTimestamp(dateBefore, transaction21["timestamp"] as String, dateAfter)
        assert Utils.checkTimestamp(dateBefore, transaction22["timestamp"] as String, dateAfter)
    }

    def "success money send to different users"() {
        given: "user and two friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def username3 = DataGenerator.createValidUsername()
        def password3 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)
        def userId3 = RequestUtils.registerUser(username3, password3, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)
        RequestUtils.sendFriendRequest(username1, password1, userId3)
        RequestUtils.sendFriendRequest(username2, password2, userId1)
        RequestUtils.sendFriendRequest(username3, password3, userId1)

        def token1 = RequestUtils.getToken(username1, password1)
        def money2 = 100
        def money3 = 200
        def message2 = DataGenerator.createValidMessage()
        def message3 = DataGenerator.createValidMessage()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: [transactions: [[toUserId: userId2,
                                       money: money2,
                                       message: message2],
                                      [toUserId: userId3,
                                       money: money3,
                                       message: message3]]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "debts are correct"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        def debts3 = RequestUtils.getDebts(username3, password3)
        def sent1 = debts1["sent"] as JSONArray
        assert sent1.contains(JSONObject.fromObject(['userId': userId2, 'amount': money2]))
        assert sent1.contains(JSONObject.fromObject(['userId': userId3, 'amount': money3]))
        assert debts1["received"] == new JSONArray()
        assert debts2["sent"] == new JSONArray()
        assert debts2["received"] == JSONArray.fromObject(['userId': userId1, 'amount': money2])
        assert debts3["sent"] == new JSONArray()
        assert debts3["received"] == JSONArray.fromObject(['userId': userId1, 'amount': money3])

        and: "current user history is correct"
        def history1 = RequestUtils.getHistory(username1, password1)
        assert history1.size() == 2
        def transaction11 = Utils.findTransaction(history1, userId2, TransactionDTOType.OUTGOING, money2, message2)
        def transaction12 = Utils.findTransaction(history1, userId3, TransactionDTOType.OUTGOING, money3, message3)
        assert transaction11 != null
        assert transaction12 != null
        assert Utils.checkTimestamp(dateBefore, transaction11["timestamp"] as String, dateAfter)
        assert Utils.checkTimestamp(dateBefore, transaction12["timestamp"] as String, dateAfter)

        and: "friend history is correct"
        def history2 = RequestUtils.getHistory(username2, password2)
        def history3 = RequestUtils.getHistory(username3, password3)
        assert history2.size() == 1
        assert history3.size() == 1
        def transaction2 = Utils.findTransaction(history2, userId1, TransactionDTOType.INCOMING, money2, message2)
        def transaction3 = Utils.findTransaction(history3, userId1, TransactionDTOType.INCOMING, money3, message3)
        assert transaction2 != null
        assert transaction3 != null
        assert Utils.checkTimestamp(dateBefore, transaction2["timestamp"] as String, dateAfter)
        assert Utils.checkTimestamp(dateBefore, transaction3["timestamp"] as String, dateAfter)
    }

    def "success money send without message"() {
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

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: [transactions: [[toUserId: userId2,
                                       money: money]]],
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
        def transaction1 = Utils.findTransaction(history1, userId2, TransactionDTOType.OUTGOING, money, null)
        def transaction2 = Utils.findTransaction(history2, userId1, TransactionDTOType.INCOMING, money, null)
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
                body:  [transactions: [[toUserId: userId2,
                                        money: money]]],
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

    def "invalid user id money send"() {
        given: "user and invalid user id"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)

        def token1 = RequestUtils.getToken(username1, password1)
        def money = 100

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [transactions: [[toUserId: toUserId,
                                        money: money]]],
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
        toUserId << [null, UUID.randomUUID().toString()]
    }

    def "empty transactions money send"() {
        given: "user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)

        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [transactions: empty],
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
        empty << [null, []]
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
                body:  [transactions: [[toUserId: userId2,
                                        money: money]]],
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
                body:  [transactions: [[toUserId: userId2,
                                        money: money]]],
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
                body:  [transactions: [[toUserId: userId2,
                                        money: money]]],
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
                body:  [transactions: [[toUserId: userId2,
                                        money: money]]],
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
                body:  [transactions: [[toUserId: toUserId,
                                        money: money]]],
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