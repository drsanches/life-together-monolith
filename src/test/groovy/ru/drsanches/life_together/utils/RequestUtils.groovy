package ru.drsanches.life_together.utils

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import net.sf.json.JSONArray
import net.sf.json.JSONObject

class RequestUtils {

    static final String SERVER_URL = "http://localhost"

    static final String PORT = "8080"

    static RESTClient getRestClient() {
        return new RESTClient( "$SERVER_URL:$PORT")
    }

    /**
     * Registers user and returns user id
     * @return user id
     */
    static String registerUser(String username, String password, String email) {
        try {
            HttpResponseDecorator response = getRestClient().post(
                    path: '/auth/registration',
                    body: [username: username,
                           password: password,
                           email: email],
                    requestContentType: ContentType.JSON) as HttpResponseDecorator
            return response.status == 201 ? response.getData()["id"] : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONObject getAuthInfo(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/auth/info",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONObject : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONObject getAuthInfo(String token) {
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/auth/info",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONObject : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONObject getUserProfile(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/profile",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONObject : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void changeUserProfile(String token, String firstName, String lastName) {
        getRestClient().put(
                path: '/profile',
                headers: ["Authorization": "Bearer $token"],
                body: [firstName: firstName,
                       lastName: lastName],
                requestContentType: ContentType.JSON)
    }

    static void sendFriendRequest(String username, String password, String userId) {
        String token = getToken(username, password)
        getRestClient().post(
                path: "/friends/manage/$userId",
                headers: ["Authorization": "Bearer $token"],
                requestContentType: ContentType.JSON)
    }

    static void deleteFriendRequest(String username, String password, String userId) {
        String token = getToken(username, password)
        getRestClient().delete(
                path: "/friends/manage/$userId",
                headers: ["Authorization": "Bearer $token"],
                requestContentType: ContentType.JSON)
    }

    static JSONArray getIncomingRequests(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/friends/requests/incoming",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONArray : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONArray getOutgoingRequests(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/friends/requests/outgoing",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONArray : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONArray getFriends(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/friends",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONArray : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void deleteUser(String username, String password) {
        String token = getToken(username, password)
        getRestClient().post(
                path: "/auth/deleteUser",
                headers: ["Authorization": "Bearer $token"],
                body:  [password: password],
                requestContentType : ContentType.JSON)
    }

    static Date[] sendMoney(String token, String[] toUserIds, Integer money, String message) {
        Date dateBefore = new Date()
        getRestClient().post(
                path: '/debts/send',
                headers: ["Authorization": "Bearer $token"],
                body: [toUserIds: toUserIds,
                       money: money,
                       message: message],
                requestContentType: ContentType.JSON)
        Date dateAfter = new Date()
        return [dateBefore, dateAfter]
    }

    static JSONObject getDebts(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/debts",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONObject : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONArray getHistory(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/debts/history",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONArray : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static String getToken(String username, String password) {
        try {
            HttpResponseDecorator response = getRestClient().post(
                    path: "/auth/login",
                    body: ["username": username,
                            "password": password],
                    requestContentType : ContentType.JSON) as HttpResponseDecorator
            return response.status == 200 ? response.getData()["accessToken"] : null
        } catch (Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static String getRefreshToken(String username, String password) {
        try {
            HttpResponseDecorator response = getRestClient().post(
                    path: "/auth/login",
                    body: ["username": username,
                           "password": password],
                    requestContentType : ContentType.JSON) as HttpResponseDecorator
            return response.status == 200 ? response.getData()["refreshToken"] : null
        } catch (Exception e) {
            e.printStackTrace()
            return null
        }
    }
}