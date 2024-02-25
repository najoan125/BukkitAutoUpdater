package com.hyfata.bukkit.autoupdate;

import com.hyfata.file.utils.FileDownloader;
import com.hyfata.file.utils.FileUtil;
import com.hyfata.file.utils.INIFileUtil;
import com.hyfata.json.JsonReader;
import com.hyfata.json.exceptions.JsonEmptyException;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class BukkitUpdater {
    public static final int PURPUR = 0;
    public static final int PAPER = 1;

    private final String version;
    private final String iniStr;
    private final String jarStr;

    String localDir = System.getProperty("user.dir");

    public BukkitUpdater(String version, int bukkit) {
        this.version = version;
        switch (bukkit) {
            case PURPUR:
                iniStr = "purpur.ini";
                jarStr = "purpur.jar";
                purpur();
                break;
            case PAPER:
                iniStr = "paper.ini";
                jarStr = "paper.jar";
                paper();
                break;
            default:
                iniStr = "";
                jarStr = "";
                break;
        }
    }

    private void purpur() {
        boolean updating = false;
        try {
            if (!isPurpurValid()) {
                printServerIsNotValid("Purpur");
                exit();
            }
            File localIniFile = new File(localDir, iniStr);
            int localVersion = getLocalVersion(localIniFile);
            int latestVersion = getPurpurLatestVersion();

            File localJarFile = new File(localDir, jarStr);
            if (latestVersion != localVersion || !localJarFile.exists()) {
                updating = true;
                System.out.println("업데이트 다운로드 중...");
                FileDownloader.download("https://api.purpurmc.org/v2/purpur/" + version + "/latest/download", localJarFile.getPath());
                FileUtil.writeFile(localIniFile.getPath(), "[version]\nversion=" + latestVersion);
                System.out.println("업데이트 완료!");
            } else {
                System.out.println("업데이트 없음.");
            }
        } catch (ConfigurationException | IOException | JsonEmptyException e) {
            if (updating) {
                System.out.println("업데이트 다운로드 도중 오류 발생!");
                System.out.println(e.getMessage());
                System.out.println("다시 시도 중...");
                purpur();
            } else {
                System.out.println("업데이트 확인 도중 오류 발생!");
                System.out.println(e.getMessage());
                exit();
            }
        }
    }

    private void paper() {
        boolean updating = false;
        try {
            if (!isPaperValid()) {
                printServerIsNotValid("Paper");
                exit();
            }
            File localIniFile = new File(localDir, iniStr);
            int localVersion = getLocalVersion(localIniFile);
            int latestVersion = getPaperLatestVersion();

            File localJarFile = new File(localDir, jarStr);
            if (latestVersion != localVersion || !localJarFile.exists()) {
                updating = true;
                System.out.println("업데이트 다운로드 중...");
                FileDownloader.download("https://api.papermc.io/v2/projects/paper/versions/" + version + "/builds/" + latestVersion + "/downloads/paper-" + version + "-" + latestVersion + ".jar", localJarFile.getPath());
                FileUtil.writeFile(localIniFile.getPath(), "[version]\nversion=" + latestVersion);
                System.out.println("업데이트 완료!");
            } else {
                System.out.println("업데이트 없음.");
            }
        } catch (ConfigurationException | IOException | JsonEmptyException e) {
            if (updating) {
                System.out.println("업데이트 다운로드 도중 오류 발생!");
                System.out.println(e.getMessage());
                System.out.println("다시 시도 중...");
                paper();
            } else {
                System.out.println("업데이트 확인 도중 오류 발생!");
                System.out.println(e.getMessage());
                exit();
            }
        }
    }

    //purpur
    private int getPurpurLatestVersion() throws JsonEmptyException, IOException {
        JSONObject json = JsonReader.readFromURL("https://api.purpurmc.org/v2/purpur/" + version);
        String latest = json.getJSONObject("builds").getString("latest");
        return Integer.parseInt(latest);
    }


    private boolean isPurpurValid() throws JsonEmptyException, IOException {
        JSONObject json = JsonReader.readFromURL("https://api.purpurmc.org/v2/purpur/" + version);
        return !json.has("error");
    }

    //paper
    private int getPaperLatestVersion() throws JsonEmptyException, IOException {
        JSONObject json = JsonReader.readFromURL("https://api.papermc.io/v2/projects/paper/versions/" + version);
        JSONArray jsonArray = Objects.requireNonNull(json).getJSONArray("builds");
        return jsonArray.getInt(jsonArray.length() - 1);
    }

    private boolean isPaperValid() throws JsonEmptyException, IOException {
        JSONObject json = JsonReader.readFromURL("https://api.papermc.io/v2/projects/paper/versions/" + version);
        return !json.has("error");
    }

    private int getLocalVersion(File iniFile) throws ConfigurationException, IOException {
        int localVersion = 0;
        if (iniFile.exists()) {
            INIConfiguration localIniConfig = INIFileUtil.getFromFile(iniFile.getPath());
            localVersion = localIniConfig.getSection("version").getInt("version");
        }
        return localVersion;
    }

    private void printServerIsNotValid(String bukkit) {
        System.out.println("<서버 구동 불가>");
        System.out.println("아직은 " + version + " 버전의 " + bukkit + " 서버가 출시되지 않았기 때문에");
        System.out.println("서버를 구동할 수 없습니다! 나중에 다시 시도해주세요!");
    }

    private void exit() {
        try {
            System.out.println("5초 후 종료...");
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {}
        System.exit(1);
    }
}
