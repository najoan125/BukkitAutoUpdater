import com.hyfata.file.utils.FileDownloader;
import com.hyfata.file.utils.FileUtil;
import com.hyfata.file.utils.INIFileUtil;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class PaperUpdater {
    private final String version;

    public PaperUpdater(String version) {
        this.version = version;
        start();
    }

    private void start() {
        try {
            URL bat = new URL("http://132.226.170.151/file/public-bukkit/" + version.replace('.', '-') + "/paper.bat");
            InputStream inputStream = bat.openStream();
            String content = FileUtil.readFromInputStream(inputStream);
            if (content.equals("set localp=no")) {
                System.out.println("<서버 구동 불가>");
                System.out.println("아직은 " + version + " 버전의 페이퍼(Paper) 서버가 출시되지 않았거나,");
                System.out.println("페이퍼(Paper) 서버가 업데이트 서버에 업로드 되지 않았기 때문에");
                System.out.println("서버를 구동할 수 없습니다! 나중에 다시 시도해주세요! (현재, 스피곳은 구동 가능)");
                exit();
            } else {
                updateCheck();
            }
        } catch (IOException e) {
            System.out.println("업데이트 확인 도중 오류 발생!");
            System.out.println(e.getMessage());
            exit();
        }
    }

    private void updateCheck() {
        try {
            INIConfiguration ini = INIFileUtil.getFromURL("http://132.226.170.151/file/public-bukkit/" + version.replace('.', '-') + "/paper.ini");
            int latest = ini.getSection("version").getInt("version");

            String currentPath = System.getProperty("user.dir");
            File paperIni = new File(currentPath, "paper.ini");
            int current = 0;
            if (paperIni.exists()) {
                INIConfiguration currentIni = INIFileUtil.getFromFile(paperIni.getPath());
                current = currentIni.getSection("version").getInt("version");
            }

            File paper = new File(currentPath, "paper.jar");
            if (latest != current || !paper.exists()) {
                System.out.println("업데이트 다운로드 중...");
                FileDownloader.download("http://132.226.170.151/file/public-bukkit/" + version.replace('.', '-') + "/paper.jar", paper.getPath());
                FileDownloader.download("http://132.226.170.151/file/public-bukkit/" + version.replace('.', '-') + "/paper.ini", paperIni.getPath());
            }
        } catch (IOException e) {
            System.out.println("업데이트 다운로드 도중 오류 발생!");
            System.out.println(e.getMessage());
            System.out.println("다시 시도 중...");
            updateCheck();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private void exit() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {}
        System.exit(1);
    }
}
