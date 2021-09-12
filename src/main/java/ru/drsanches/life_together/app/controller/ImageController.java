package ru.drsanches.life_together.app.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.drsanches.life_together.app.service.web.ImageWebService;

@RestController
@RequestMapping(value = "/api/v1/image")
public class ImageController {

    @Autowired
    private ImageWebService imageWebService;

    @RequestMapping(path = "/avatar", method = RequestMethod.POST)
    @ApiOperation(value = "Adds new avatar")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadAvatar(@ApiParam(required = true, value = "Multipart file with image") //TODO: Fix data type in swagger
                             @RequestParam(value = "file", required = false) MultipartFile file) {
        imageWebService.uploadAvatar(file);
    }

    @RequestMapping(path = "/{imageId}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiOperation(value = "Returns an image by id")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public byte[] getImage(@PathVariable String imageId) {
        return imageWebService.getImage(imageId);
    }

    @RequestMapping(path = "/avatar", method = RequestMethod.DELETE)
    @ApiOperation(value = "Removes an avatar for user")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public void deleteAvatar() {
        imageWebService.deleteAvatar();
    }
}