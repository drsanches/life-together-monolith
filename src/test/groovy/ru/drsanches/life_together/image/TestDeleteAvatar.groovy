package ru.drsanches.life_together.image

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.life_together.utils.DataGenerator
import ru.drsanches.life_together.utils.RequestUtils
import ru.drsanches.life_together.utils.Utils
import spock.lang.Specification

class TestDeleteAvatar extends Specification {

    String PATH = "/api/v1/image/avatar"

    def "successful default avatar deletion"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        def response = RequestUtils.getRestClient().delete(
                path: PATH,
                headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert RequestUtils.getUserProfile(username, password)["imagePath"] == Utils.getDefaultImagePath()

        and: "image does not change"
        def imageData = RequestUtils.getImage(username, password, Utils.getDefaultImagePath())
        assert Utils.checkImage(imageData, "default.jpg")
    }

    def "successful avatar deletion"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)
        RequestUtils.uploadAvatar(token, "test1.jpg")
        def imagePath = RequestUtils.getUserProfile(username, password)["imagePath"] as String

        when: "request is sent"
        def response = RequestUtils.getRestClient().delete(
                path: PATH,
                headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert RequestUtils.getUserProfile(username, password)["imagePath"] == Utils.getDefaultImagePath()

        and: "image is correct"
        def imageData = RequestUtils.getImage(username, password, Utils.getDefaultImagePath())
        assert Utils.checkImage(imageData, "default.jpg")

        and: "the old image is available"
        def oldImageData = RequestUtils.getImage(username, password, imagePath)
        assert Utils.checkImage(oldImageData, "test1.jpg")
    }

    def "delete avatar with invalid token"() {
        given: "user and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().delete(
                path: PATH,
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}