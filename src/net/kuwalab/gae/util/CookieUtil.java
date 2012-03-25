package net.kuwalab.gae.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * クッキー用のユーティリティ
 * 
 * @author kuwalab
 * 
 */
public class CookieUtil {
    private CookieUtil() {
    }

    /**
     * クッキーの保管ユーティリティ
     * 
     * @param response
     * @param name
     *            クッキーの名称
     * @param value
     *            クッキーの値
     * @param expiry
     *            有効期限
     */
    public static void setCookie(HttpServletResponse response, String name,
            String value, int expiry) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(expiry);
        response.addCookie(cookie);
    }

    /**
     * クッキーの読み取り。存在しない名称の場合はnullを返す。
     * 
     * @param request
     * @param name
     *            クッキーの名称
     * @return クッキーの名称に対する値
     */
    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
