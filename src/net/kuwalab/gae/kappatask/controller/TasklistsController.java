package net.kuwalab.gae.kappatask.controller;

import net.kuwalab.google.util.AuthToken;
import net.kuwalab.google.util.TasksUtil;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class TasklistsController extends Controller {

    @Override
    public Navigation run() throws Exception {
        if (asString("error") != null) {
            return forward("error.jsp");
        }
        AuthToken authToken = TasksUtil.retrieveTokens(asString("code"));

        requestScope(
            "taskLists",
            TasksUtil.tasklistsList(authToken.getAccess_token()));
        requestScope("access_token", authToken.getAccess_token());

        return forward("tasklists.jsp");
    }
}
