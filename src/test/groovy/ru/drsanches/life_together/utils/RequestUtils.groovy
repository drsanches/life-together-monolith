package ru.drsanches.life_together.utils

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import net.sf.json.JSONObject

class RequestUtils {

    static final String SERVER_URL = "http://localhost"

    static final String PORT = "8080"

    static RESTClient getRestClient() {
        return new RESTClient( "$SERVER_URL:$PORT")
    }

    static void registerUser(String username, String password, String email) {
        getRestClient().post(
                path: '/auth/registration',
                body: [username: username,
                       password: password,
                       email: email],
                 requestContentType: ContentType.JSON)
    }

    static JSONObject getAuthInfo(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/auth/info",
                    headers: ["Authorization": "Bearer $token"])
            return response.status == 200 ? response.getData() : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONObject getAuthInfo(String token) {
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/auth/info",
                    headers: ["Authorization": "Bearer $token"])
            return response.status == 200 ? response.getData() : null
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
                    path: "/user",
                    headers: ["Authorization": "Bearer $token"])
            return response.status == 200 ? response.getData() : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void changeUserProfile(String token, String firstName, String lastName) {
        getRestClient().put(
                path: '/user',
                headers: ["Authorization": "Bearer $token"],
                body: [firstName: firstName,
                       lastName: lastName],
                requestContentType: ContentType.JSON)
    }

    static String getToken(String username, String password) {
        try {
            HttpResponseDecorator response = getRestClient().post(
                    path: "/auth/login",
                    body: ["username": username,
                            "password": password],
                    requestContentType : ContentType.JSON)
            return response.status == 200 ? response.getData()["access_token"] : null
        } catch (Exception e) {
            e.printStackTrace()
            return null
        }
    }
}