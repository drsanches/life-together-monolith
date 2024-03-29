package ru.drsanches.life_together.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
    @Operation(summary = "Adds new avatar")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadAvatar(@Parameter(required = true, description = "Multipart file with image") //TODO: Fix data type in swagger
                             @RequestParam(value = "file", required = false) MultipartFile file) {
        imageWebService.uploadAvatar(file);
    }

    @RequestMapping(path = "/{imageId}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "Returns an image by id")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public byte[] getImage(@PathVariable String imageId) {
        return imageWebService.getImage(imageId);
    }

    @RequestMapping(path = "/avatar", method = RequestMethod.DELETE)
    @Operation(summary = "Removes an avatar for user")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public void deleteAvatar() {
        imageWebService.deleteAvatar();
    }
}