package net.kuwalab.google.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.arnx.jsonic.JSON;

/**
 * Google Tasks API用のユーティリティ。<br>
 * 学習用に生APIを操作。
 * 
 * @author kuwalab
 */
public class TasksUtil {
    private static final String OAUTH_URL =
        "https://accounts.google.com/o/oauth2/auth";

    private static final String CLIENT_ID;
    private static final String CLIENT_SECRET;
    private static final String REDIRECT_URI;
    // アプリケーションの場合には以下。
    // private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static final String SCOPES;
    private static final String ENDPOINT;

    private static final String ENCODE_UTF8 = "utf-8";

    private static String urlEncode(String target) {
        String result = null;
        try {
            result = URLEncoder.encode(target, ENCODE_UTF8);
        } catch (UnsupportedEncodingException e) {
            // 無視
        }

        return result;
    }

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("gae");
        CLIENT_ID = bundle.getString("CLIENT_ID");
        CLIENT_SECRET = bundle.getString("CLIENT_SECRET");
        REDIRECT_URI = bundle.getString("REDIRECT_URI");
        SCOPES = bundle.getString("SCOPES");
        ENDPOINT = bundle.getString("ENDPOINT");
    }

    /**
     * 認証用のアドレスを返す。
     * 
     * @return 認証用のアドレス
     * @throws IOException
     */
    public static String getOAuthAddress() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(OAUTH_URL);
        sb.append("?response_type=code");
        sb.append("&client_id=").append(urlEncode(CLIENT_ID));
        sb.append("&redirect_uri=").append(urlEncode(REDIRECT_URI));
        sb.append("&scope=").append(urlEncode(SCOPES));
        sb.append("&stat=dummy");

        return sb.toString();
    }

    /**
     * 認証トークンを取得する
     * 
     * @param authorizationCode
     *            認証コード
     * @return 認証トークン
     * @throws IOException
     */
    public static AuthToken retrieveTokens(String authorizationCode)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("code=").append(urlEncode(authorizationCode));
        sb.append("&client_id=").append(urlEncode(CLIENT_ID));
        sb.append("&client_secret=").append(urlEncode(CLIENT_SECRET));
        sb.append("&redirect_uri=").append(urlEncode(REDIRECT_URI));
        sb.append("&grant_type=authorization_code");
        byte[] payload = sb.toString().getBytes();

        // POST メソッドでリクエストする
        HttpURLConnection c =
            (HttpURLConnection) new URL(ENDPOINT + "/token").openConnection();
        c.setRequestMethod("POST");
        c.setDoOutput(true);
        c.setRequestProperty("Content-Length", String.valueOf(payload.length));
        c.getOutputStream().write(payload);
        c.getOutputStream().flush();

        // トークン類が入ったレスポンスボディの内容を返す(JSONで返される)
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(c.getInputStream()));
        AuthToken authToken = JSON.decode(reader, AuthToken.class);
        reader.close();

        return authToken;
    }

    /**
     * タスクリストの一覧を取得する
     * 
     * @param accessToken
     *            認証トークン
     * @return タスクリストの一覧
     * @throws IOException
     */
    public static TaskLists tasklistsList(String accessToken)
            throws IOException {
        HttpURLConnection c =
            (HttpURLConnection) new URL(
                "https://www.googleapis.com/tasks/v1/users/@me/lists")
                .openConnection();
        c.setRequestProperty("Authorization", "OAuth " + accessToken);

        return JSON.decode(
            InputStreamUtil.readAndClose(c.getInputStream()),
            TaskLists.class);
    }

    /**
     * タスクの一覧を取得する
     * 
     * @param accessToken
     *            認証トークン
     * @param id
     *            取得するタスクリストのid
     * @return タスクの一覧
     * @throws IOException
     */
    public static Tasks tasksList(String accessToken, String id)
            throws IOException {
        String request =
            "https://www.googleapis.com/tasks/v1/lists/" + id + "/tasks";
        HttpURLConnection c =
            (HttpURLConnection) new URL(request).openConnection();
        c.setRequestProperty("Authorization", "OAuth " + accessToken);

        Map<String, Integer> levelMap = new HashMap<String, Integer>();
        Tasks tasks =
            JSON.decode(
                InputStreamUtil.readAndClose(c.getInputStream()),
                Tasks.class);

        for (Task task : tasks.getItems()) {
            if (task.getParent() == null || task.getParent().equals("")) {
                levelMap.put(task.getId(), 1);
                task.setLevel(1);
            } else {
                int level = levelMap.get(task.getParent()) + 1;
                levelMap.put(task.getId(), level);
                task.setLevel(level);
            }
        }

        return tasks;
    }

    /**
     * 指定のタスクを取得する
     * 
     * @param accessToken
     *            認証トークン
     * @param tasklist
     *            タスクリストのID
     * @param task
     *            タスクのID
     * @return 指定のタスク
     * @throws IOException
     */
    public static Task tasksTask(String accessToken, String tasklist,
            String task) throws IOException {
        String request =
            "https://www.googleapis.com/tasks/v1/lists/"
                + tasklist
                + "/tasks/"
                + task;

        HttpURLConnection c =
            (HttpURLConnection) new URL(request).openConnection();
        c.setRequestProperty("Authorization", "OAuth " + accessToken);

        return JSON.decode(
            InputStreamUtil.readAndClose(c.getInputStream()),
            Task.class);
    }

    /**
     * タスクの更新
     * 
     * @param accessToken
     *            認証トークン
     * @param tasklist
     *            タスクリストのID
     * @param task
     *            更新するタスク
     * @throws IOException
     */
    public static void tasksUpdate(String accessToken, String tasklist,
            Task task) throws IOException {
        String request =
            "https://www.googleapis.com/tasks/v1/lists/"
                + tasklist
                + "/tasks/"
                + task.getId();
        HttpURLConnection c =
            (HttpURLConnection) new URL(request).openConnection();
        c.setRequestMethod("PUT");
        c.setRequestProperty("Authorization", "OAuth " + accessToken);
        c.setRequestProperty("Content-Type", "application/json");
        String json = JSON.encode(task);
        c.setDoOutput(true);
        c.setRequestProperty("Content-Length", String.valueOf(json.length()));
        c.getOutputStream().write(json.getBytes());
        c.getOutputStream().flush();

        c.getInputStream();
    }
}
