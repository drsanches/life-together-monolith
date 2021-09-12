package ru.drsanches.life_together.app.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.life_together.app.data.image.model.Image;
import ru.drsanches.life_together.app.service.domain.ImageDomainService;
import ru.drsanches.life_together.exception.server.ServerError;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class DefaultImageInitializer {

    private final Logger LOG = LoggerFactory.getLogger(DefaultImageInitializer.class);

    @Autowired
    private ImageDomainService imageDomainService;

    public void initialize(String filename) {
        if (imageDomainService.exists("default")) {
            LOG.info("Default image is already initialized");
            return;
        }
        try {
            imageDomainService.saveImage(new Image("default", new FileInputStream(new File(filename)).readAllBytes()));
            LOG.info("Default image has been initialized");
        } catch (IOException e) {
            throw new ServerError("Default image loading error ", e);
        }
    }
}