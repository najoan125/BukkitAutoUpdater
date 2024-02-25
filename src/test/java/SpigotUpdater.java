import com.hyfata.file.utils.FileDownloader;
import com.hyfata.file.utils.INIFileUtil;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.io.IOException;

public class SpigotUpdater {
    private final String version;

    public SpigotUpdater(String version) {
        this.version = version;
        updateCheck();
    }

    private void updateCheck() {
        try {
            INIConfiguration ini = INIFileUtil.getFromURL("http://132.226.170.151/file/public-bukkit/" + version.replace('.', '-') + "/version.ini");
            int latest = ini.getSection("version").getInt("version");

            String currentPath = System.getProperty("user.dir");
            File spigotIni = new File(currentPath, "version.ini");
            INIConfiguration currentIni = INIFileUtil.getFromFile(spigotIni.getPath());
            int current = 0;
            if (spigotIni.exists()) {
                current = currentIni.getSection("version").getInt("version");
            }

            File spigot = new File(currentPath, "spigot.jar");
            if (latest != current || !spigot.exists()) {
                System.out.println("업데이트 다운로드 중...");
                FileDownloader.download("http://132.226.170.151/file/public-bukkit/" + version.replace('.', '-') + "/spigot.jar", spigot.getPath());
                FileDownloader.download("http://132.226.170.151/file/public-bukkit/" + version.replace('.', '-') + "/version.ini", spigotIni.getPath());
            }
        } catch (IOException | ConfigurationException e) {
            System.out.println("업데이트 확인 도중 오류 발생!");
            System.out.println(e.getMessage());
            exit();
        }
    }

    private void exit() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {}
        System.exit(1);
    }
}
