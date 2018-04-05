package no.aegisdynamics.habitat.util;

/*
 * A wrapper for handling image files.
 */

import java.io.IOException;

public interface ImageFile {
    void create(String name, String extension) throws IOException;

    boolean exists();

    void delete();

    String getPath();

}
