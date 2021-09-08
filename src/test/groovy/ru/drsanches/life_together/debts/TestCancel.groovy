package ru.drsanches.life_together.debts

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import ru.drsanches.life_together.app.data.debts.dto.TransactionDTOType
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import ru.drsanches.life_together.utils.Utils
import spock.lang.Specification

class TestCancel extends Specification {

    String PATH = "/api/v1/debts/cancel"

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

        def message = DataGenerator.createValidMessage()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [userId: userId2,
                        message: message],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new transaction"
        def transactions1 = RequestUtils.getHistory(username1, password1)
        assert transactions1.size() == 2
        def transaction1 = Utils.findTransaction(transactions1, userId2, TransactionDTOType.CANCELED_BY_CURRENT, money, message)
        assert transaction1 != null
        assert Utils.checkTimestamp(dateBefore, transaction1["timestamp"] as String, dateAfter)

        and: "friend history contains new transaction"
        def transactions2 = RequestUtils.getHistory(username2, password2)
        assert transactions2.size() == 2
        def transaction2 = Utils.findTransaction(transactions2, userId1, TransactionDTOType.CANCELED_BY_OTHER, money, message)
        assert transaction2 != null
        assert Utils.checkTimestamp(dateBefore, transaction2["timestamp"] as String, dateAfter)
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

        def message = DataGenerator.createValidMessage()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [userId: userId2,
                        message: message],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new transaction"
        def transactions = RequestUtils.getHistory(username1, password1)
        assert transactions.size() == 2
        def transaction = Utils.findTransaction(transactions, userId2, TransactionDTOType.CANCELED_BY_CURRENT, money, message)
        assert transaction != null
        assert Utils.checkTimestamp(dateBefore, transaction["timestamp"] as String, dateAfter)


        and: "friend history contains new transaction"
        def transactions2 = RequestUtils.getHistory(username2, password2)
        assert transactions2.size() == 2
        def transaction2 = Utils.findTransaction(transactions2, userId1, TransactionDTOType.CANCELED_BY_OTHER, money, message)
        assert transaction2 != null
        assert Utils.checkTimestamp(dateBefore, transaction2["timestamp"] as String, dateAfter)
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

        def message = DataGenerator.createValidMessage()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [userId: userId2,
                        message: message],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new transaction"
        def transactions = RequestUtils.getHistory(username1, password1)
        assert transactions.size() == 2
        def transaction = Utils.findTransaction(transactions, userId2, TransactionDTOType.CANCELED_BY_CURRENT, money, message)
        assert transaction != null
        assert Utils.checkTimestamp(dateBefore, transaction["timestamp"] as String, dateAfter)

        and: "other user history contains new transaction"
        def transactions2 = RequestUtils.getHistory(username2, password2)
        assert transactions2.size() == 2
        def transaction2 = Utils.findTransaction(transactions2, userId1, TransactionDTOType.CANCELED_BY_OTHER, money, message)
        assert transaction2 != null
        assert Utils.checkTimestamp(dateBefore, transaction2["timestamp"] as String, dateAfter)
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

        def message = DataGenerator.createValidMessage()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [userId: userId2,
                        message: message],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new transaction"
        def transactions = RequestUtils.getHistory(username1, password1)
        assert transactions.size() == 2
        def transaction = Utils.findTransaction(transactions, userId2, TransactionDTOType.CANCELED_BY_CURRENT, money, message)
        assert transaction != null
        assert Utils.checkTimestamp(dateBefore, transaction["timestamp"] as String, dateAfter)

        and: "other user history contains new transaction"
        def transactions2 = RequestUtils.getHistory(username2, password2)
        assert transactions2.size() == 2
        def transaction2 = Utils.findTransaction(transactions2, userId1, TransactionDTOType.CANCELED_BY_OTHER, money, message)
        assert transaction2 != null
        assert Utils.checkTimestamp(dateBefore, transaction2["timestamp"] as String, dateAfter)
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

        def message = DataGenerator.createValidMessage()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [userId: userId2,
                        message: message],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new transaction"
        def transactions = RequestUtils.getHistory(username1, password1)
        assert transactions.size() == 2
        def transaction = Utils.findTransaction(transactions, userId2, TransactionDTOType.CANCELED_BY_CURRENT, money, message)
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

        def message = DataGenerator.createValidMessage()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [userId: userId2,
                        message: message],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new transaction"
        def transactions = RequestUtils.getHistory(username1, password1)
        assert transactions.size() == 2
        def transaction = Utils.findTransaction(transactions, userId2, TransactionDTOType.CANCELED_BY_CURRENT, money, message)
        assert transaction != null
        assert Utils.checkTimestamp(dateBefore, transaction["timestamp"] as String, dateAfter)
    }

    def "success cancel debt without message"() {
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
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [userId: userId2,
                        message: empty],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 200

        and: "debts are empty"
        def debts = RequestUtils.getDebts(username1, password1)
        assert debts["sent"] == new JSONArray()
        assert debts["received"] == new JSONArray()

        and: "history contains new transaction"
        def transactions = RequestUtils.getHistory(username1, password1)
        assert transactions.size() == 2
        def transaction = Utils.findTransaction(transactions, userId2, TransactionDTOType.CANCELED_BY_CURRENT, money, message)
        assert transaction != null
        assert Utils.checkTimestamp(dateBefore, transaction["timestamp"] as String, dateAfter)

        and: "friend history contains new transaction"
        def transactions2 = RequestUtils.getHistory(username2, password2)
        assert transactions2.size() == 2
        def transaction2 = Utils.findTransaction(transactions2, userId1, TransactionDTOType.CANCELED_BY_OTHER, money, message)
        assert transaction2 != null
        assert Utils.checkTimestamp(dateBefore, transaction2["timestamp"] as String, dateAfter)

        where:
        empty << [null, ""]
        message << [JSONNull.getInstance().toString(), ""]
    }

    def "cancel debt without userId"() {
        given: "user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [userId: empty],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        def e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        empty << [null, ""]
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
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [userId: userId2],
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
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [userId: userId2],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        def e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "cancel debt to yourself"() {
        given: "user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [userId: userId1],
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
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [userId: userId2],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        def e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}