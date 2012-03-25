package net.kuwalab.google.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamUtil {
    private InputStreamUtil() {
    }

    /**
     * InputStreamを行単位に読み込み、行ごとに\r\nを付加し連結して返す。<br>
     * 最後にReaderはcloseする。<br>
     * JSON.decodeはStringでなくてもBufferedReaderを直接読めるが、デバッグの観点もあり実装した。<br>
     * 必要なら、戻り値を出力して確認できる。
     * 
     * @param is
     *            読み込むBufferedReader
     * @return 読み込んだ文字列
     * @throws IOException
     */
    public static String readAndClose(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\r\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return sb.toString();
    }
}
