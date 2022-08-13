package org.vmy;

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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class CheckUpdater {
    public static void main (String[] args) throws Exception {

        Parameters p = Parameters.getInstance();

        System.out.println("Checking for updates...");

        try {

            //GET GITHUB RELEASE INFO
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
                return;
            System.out.println("Github Repo: Found");

            //GET DOWNLOAD URL
            String jarUrl = null;
            try {
                JSONObject jsonTop = new JSONObject(jsonTxt);
                JSONArray assets = jsonTop.getJSONArray("assets");
                for (Object ao : assets.toList()) {
                    HashMap asset = ((HashMap) ao);
                    String thisUrl = (String) asset.get("browser_download_url");
                    if (thisUrl != null && thisUrl.endsWith(".jar"))
                        jarUrl = thisUrl;
                }
                if (jarUrl==null)
                    throw new Exception("JAR download url was not found." );
                System.out.println("Latest JAR path: " + jarUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

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

            //DOWNLOAD LATEST
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
                System.out.println("Latest JAR downloaded: " +tempJarFile.getName() + " " + tempJarFile.length() + " bytes");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            //SET CURRENT VERSION
            try (OutputStream os = new FileOutputStream(verFile)) {
                IOUtils.write(latestVersion.getBytes(StandardCharsets.UTF_8), os);
                System.out.println("Set version: " + latestVersion);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int debug123 = 0;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}
