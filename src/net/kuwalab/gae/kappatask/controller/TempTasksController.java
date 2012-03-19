package net.kuwalab.gae.kappatask.controller;

import javax.servlet.http.Cookie;

import net.kuwalab.google.util.Task;
import net.kuwalab.google.util.Tasks;
import net.kuwalab.google.util.TasksUtil;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class TempTasksController extends Controller {

    @Override
    public Navigation run() throws Exception {
        Cookie[] cookies = request.getCookies();
        int tzoffset = 0;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("tzoffset")) {
                tzoffset = Integer.parseInt(cookie.getValue());
                break;
            }
        }
        System.out.println("tzoffset=" + tzoffset);

        Tasks tasks =
            TasksUtil.tasksList(asString("access_token"), asString("id"));
        requestScope("tasks", tasks);
        requestScope("tasklist", asString("id"));

        for (Task task : tasks.getItems()) {
            task.setTzoffset(tzoffset);
        }

        return forward("tasks.jsp");
    }
}
