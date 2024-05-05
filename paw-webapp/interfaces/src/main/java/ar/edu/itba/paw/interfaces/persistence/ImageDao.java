package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageDao {
    Optional<Image> getImage(long id);
    Image uploadImage(byte[] bytes);
}