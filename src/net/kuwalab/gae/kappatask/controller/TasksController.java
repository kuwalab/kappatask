package net.kuwalab.gae.kappatask.controller;

import java.io.PrintWriter;

import javax.servlet.http.Cookie;

import net.kuwalab.google.util.Task;
import net.kuwalab.google.util.Tasks;
import net.kuwalab.google.util.TasksUtil;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.HtmlUtil;

public class TasksController extends Controller {

    private static final String html1 =
        "<div class=\\\"option cf\\\"><span class=\\\"due\\\">";
    private static final String html2 =
        "</span><input type=\\\"hidden\\\" class=\\\"task\\\" value=\\\"";
    private static final String html3 = "\\\"></div>";

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

        Tasks tasks =
            TasksUtil.tasksList(asString("access_token"), asString("tasklist"));
        requestScope("tasks", tasks);
        requestScope("tasklist", asString("tasklist"));

        for (Task task : tasks.getItems()) {
            task.setTzoffset(tzoffset);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter pw = response.getWriter();
        pw.write("{ \"result\":\"" + createHtml(tasks) + "\"}");
        pw.flush();

        return null;
    }

    private String createHtml(Tasks tasks) {
        StringBuilder sb = new StringBuilder();

        int level = -1;
        for (Task task : tasks.getItems()) {
            if (task.getLevel() != level) {
                diffLevel(sb, task.getLevel() - level);
                level = task.getLevel();
            }

            sb
                .append("<li>")
                .append(HtmlUtil.escape(task.getTitle()))
                .append(html1);
            if (task.getDue() != null) {
                sb.append(task.getDispDue());
            }
            sb.append(html2);
            sb.append(task.getId());
            sb.append(html3).append("</li>");
        }
        diffLevel(sb, 0 - level);

        return sb.toString();
    }

    private void diffLevel(StringBuilder sb, int diff) {
        if (diff < 0) {
            for (int i = 0; i < -diff; i++) {
                sb.append("</ul>");
            }
        } else {
            sb.append("<ul>");
        }
    }
}
