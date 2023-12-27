package org.vmy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CheckUpdater {
    public static void main (String[] args) throws Exception {

        Parameters p = Parameters.getInstance();

        System.out.println("Checking for updates...");

        try {

            String jsonTxt = getGithubReleaseJson(p);
            if (jsonTxt == null) return;

            String jarUrl = getGithubUpgradeJarUrl(jsonTxt);
            if (jarUrl == null) return;

            //DETERMINE LATEST VERSION
            int downloadIndex = jarUrl.indexOf("/download/") + 10;
            int lastSlash = jarUrl.lastIndexOf("/");
            String latestVersion = jarUrl.substring(downloadIndex, lastSlash);
            System.out.println("Latest version: " + latestVersion);

            //GET CURRENT VERSION
            File verFile = new File(p.homeDir + "version.txt");
            String currentVersion = null;
            try (InputStream is = new FileInputStream(verFile)) {
                currentVersion = IOUtils.toString(is, "UTF-8");
                System.out.println("Current version: " + currentVersion);
            } catch (Exception e) {
                //continue
            }

            //EXIT IF VERSIONS MATCH
            if (latestVersion==null || latestVersion.equals(currentVersion)) {
                System.out.println("You are up to date.");
                return;
            }

            if (!downloadUpgradeJar(jarUrl))
                return;

            //SET CURRENT VERSION
            try (OutputStream os = new FileOutputStream(verFile)) {
                IOUtils.write(latestVersion.getBytes(StandardCharsets.UTF_8), os);
                System.out.println("Set version: " + latestVersion);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean downloadUpgradeJar(String jarUrl) {
        //DOWNLOAD LATEST UPGRADE JAR
        try (BufferedInputStream in = new BufferedInputStream(new URL(jarUrl).openStream())) {
            File tempJarFile = new File("zcore.jar");
            if (tempJarFile.exists())
                tempJarFile.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(tempJarFile);
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            System.out.println("Latest JAR downloaded: " + tempJarFile.getName() + " " + new DecimalFormat("#,###").format(tempJarFile.length()) + " bytes");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean downloadZPackZip(String zipUrl) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(zipUrl).openStream())) {
            File zpackFile = new File("zpack.zip");
            if (zpackFile.exists())
                zpackFile.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(zpackFile);
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            System.out.println("Latest ZPACK downloaded: " + zpackFile.getName() + " " + new DecimalFormat("#,###").format(zpackFile.length()) + " bytes");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getGithubUpgradeJarUrl(String jsonTxt) {
        String jarUrl = null;
        try {
            JSONObject jsonTop = new JSONObject(jsonTxt);
            if (!jsonTop.has("assets")) {
                System.out.println("Unable check for updates as the Github rate limit was exceeded.  Try again later.");
                return null;
            }
            JSONArray assets = jsonTop.getJSONArray("assets");
            for (Object ao : assets.toList()) {
                HashMap asset = ((HashMap) ao);
                String thisUrl = (String) asset.get("browser_download_url");
                if (thisUrl != null && thisUrl.contains("upgrade") && thisUrl.endsWith(".jar"))
                    jarUrl = thisUrl;
            }
            if (jarUrl==null)
                throw new Exception("JAR download url was not found." );
            System.out.println("Latest JAR path: " + jarUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return jarUrl;
    }

    public static String getGithubZPackUrl(String jsonTxt) {
        String zipUrl = null;
        try {
            JSONObject jsonTop = new JSONObject(jsonTxt);
            JSONArray assets = jsonTop.getJSONArray("assets");
            for (Object ao : assets.toList()) {
                HashMap asset = ((HashMap) ao);
                String thisUrl = (String) asset.get("browser_download_url");
                if (thisUrl != null && thisUrl.endsWith("zpack.zip"))
                    zipUrl = thisUrl;
            }
            if (zipUrl==null)
                throw new Exception("ZPACK download url was not found." );
            System.out.println("Latest ZPACK path: " + zipUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return zipUrl;
    }

    public static String getGithubReleaseJson(Parameters p) {
        String jsonTxt = null;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(p.repoUrl);
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            jsonTxt = EntityUtils.toString(result.getEntity(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonTxt == null)
            return null;
        System.out.println("Github Repo: Found");
        return jsonTxt;
    }

    public static void unzipFolder(Path source, Path target) throws IOException {

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(source.toFile().toPath()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                boolean isDirectory = false;
                // example 1.1
                // some zip stored files and folders separately
                // e.g data/
                //     data/folder/
                //     data/folder/file.txt
                if (zipEntry.getName().endsWith("/") || zipEntry.getName().endsWith(File.separator)) {
                    isDirectory = true;
                }

                Path newPath = zipSlipProtect(zipEntry, target);

                if (isDirectory) {
                    System.out.println("Creating directory: " + newPath);
                    Files.createDirectories(newPath);
                } else {

                    // example 1.2
                    // some zip stored file path only, need create parent directories
                    // e.g data/folder/file.txt
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }

                    // copy files, nio
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);

                    // copy files, classic
                    /*System.out.println("Copying zipped file: " + newPath.toFile());
                    try (FileOutputStream fos = new FileOutputStream(newPath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }*/
                }

                zipEntry = zis.getNextEntry();

            }
            zis.closeEntry();

        }

    }

    // protect zip slip attack
    public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir)
            throws IOException {

        // test zip slip vulnerability
        // Path targetDirResolved = targetDir.resolve("../../" + zipEntry.getName());

        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        // else throws exception
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }

        return normalizePath;
    }

    public static void syncBatFile() {
        Parameters p = Parameters.getInstance();

        try {
            File batFile = new File(p.homeDir + File.separator + "MzFightReporter.bat");
            if (batFile.length() != latestContents.length()) {
                System.out.println("Updating Batch file...");
                FileUtils.writeStringToFile(batFile, latestContents, "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String latestContents = "@echo off\n" +
            "\n" +
            "tasklist /fi \"imagename eq java.exe\" /v | find \"MzFightReporter\" 1>NUL 2>NUL\n" +
            "if %ERRORLEVEL% == 0 (\n" +
            "  echo.\n" +
            "  echo MzFightReporter is already running!  Exiting.\n" +
            "  echo.\n" +
            "  timeout /T 10 /NOBREAK\n" +
            "  exit\n" +
            ")\n" +
            "\n" +
            "title MzFightReporter Launcher\n" +
            "\n" +
            "set .\\jre8\\bin\\;PATH=%PATH%\n" +
            "\n" +
            "java -version 1>/dev/nul 2>/dev/nul\n" +
            "\n" +
            "if %ERRORLEVEL% == 0 (\n" +
            "  rem Java is available\n" +
            ") else (\n" +
            "  @echo PROBLEM DETECTED: Java could not be located.  Install at www.java.com and try again.\n" +
            "  @echo.\n" +
            "  pause\n" +
            ")\n" +
            "\n" +
            "if not DEFINED IS_MINIMIZED set IS_MINIMIZED=1 && start \"\" /min \"%~dpnx0\" %* && exit\n" +
            "\n" +
            "java -jar MzApp-Latest.jar CheckUpdater\n" +
            "\n" +
            "IF EXIST .\\zcore.jar (\n" +
            "    timeout 3\n" +
            "    move /y zcore.jar MzApp-Latest.jar\n" +
            ")\n" +
            "\n" +
            "@echo Launching UI...\n" +
            "\n" +
            "java -jar MzApp-Latest.jar FileWatcher\n" +
            "exit\n";
}
