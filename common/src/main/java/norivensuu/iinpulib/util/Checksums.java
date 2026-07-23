package norivensuu.iinpulib.util;


import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class Checksums {

    public static String sha256(Object... inputs) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            for (var input : inputs) {
                digest.update(String.valueOf(input.hashCode()).getBytes(StandardCharsets.UTF_8));
            }

            byte[] hash = digest.digest();
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String hex, String expectedHex) {
        return hex.equalsIgnoreCase(expectedHex);
    }
}