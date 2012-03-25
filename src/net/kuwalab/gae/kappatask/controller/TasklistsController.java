package net.kuwalab.gae.kappatask.controller;

import net.kuwalab.gae.util.CookieUtil;
import net.kuwalab.google.util.AuthToken;
import net.kuwalab.google.util.TasksUtil;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.StringUtil;

public class TasklistsController extends Controller {
    private static final String ROOT = "/";

    @Override
    public Navigation run() throws Exception {
        if (asString("error") != null) {
            return forward("error.jsp");
        }
        String accessToken = CookieUtil.getCookie(request, "access_token");
        if (accessToken == null) {
            // クッキーに認証情報がない場合
            String code = asString("code");
            if (StringUtil.isEmpty(code)) {
                return redirect(ROOT);
            }
            AuthToken authToken = TasksUtil.retrieveTokens(code);
            if (authToken.getAccess_token() == null) {
                return redirect(ROOT);
            }
            int expiry = Integer.parseInt(authToken.getExpires_in());
            CookieUtil.setCookie(
                response,
                "access_token",
                authToken.getAccess_token(),
                expiry);

            // リダイレクトしてURLを綺麗にする。
            return redirect("/tasklists");
        }

        // クッキーに認証情報がある場合
        requestScope("taskLists", TasksUtil.tasklistsList(accessToken));
        requestScope("access_token", accessToken);

        return forward("tasklists.jsp");
    }
}
