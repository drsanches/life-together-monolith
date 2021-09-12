package ru.drsanches.life_together.app.data.image.repository;

import org.springframework.data.repository.CrudRepository;
import ru.drsanches.life_together.app.data.image.model.Image;

public interface ImageRepository extends CrudRepository<Image, String> {}