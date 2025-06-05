package scanmycar.model.utils;

import scanmycar.Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;

public class FileUtils {

    /**
     * Checks if a file with the specified name exists.
     *
     * @param fileName Name of the file to search for.
     * @return true if the file exists, otherwise false.
     */
    public static boolean isFileFound(String fileName) {
        Path imagePath = Paths.get(fileName);
        return Files.exists(imagePath);
    }

    /**
     * Converts a colored BufferedImage to black and white (grayscale).
     *
     * @param image the original colored image
     * @return a new BufferedImage in grayscale
     */
    public static BufferedImage convertToBlackAndWhite(BufferedImage image) {
        BufferedImage bwImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = bwImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bwImage;
    }


    /**
     * Returns the path of the directory containing Tesseract's trained data.
     *
     * @return Absolute path of the trained data directory.
     */
    public static String getTrainedDataDirectory() throws URISyntaxException {
        ClassLoader classLoader = Main.class.getClassLoader();
        if (classLoader.getResource("data") == null) {
            throw new IllegalStateException("Le dossier des données Tesseract est introuvable.");
        }
        URI uriData = classLoader.getResource("data").toURI();
        return Paths.get(uriData).toString();
    }

}
