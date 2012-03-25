package net.kuwalab.gae.kappatask.controller;

import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.kuwalab.google.util.Task;
import net.kuwalab.google.util.TasksUtil;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class UpdateController extends Controller {

    @Override
    public Navigation run() throws Exception {
        String access_token = asString("access_token");
        String tasklist = asString("tasklist");
        String taskId = asString("task");
        String due = asString("due");

        Task task = TasksUtil.tasksTask(access_token, tasklist, taskId);
        if (due == null || due.equals("")) {
            task.setDue(null);
        } else {
            SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd");
            s.setLenient(true);
            Date date = null;
            try {
                date = s.parse(due);
                s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                due = s.format(date);
            } catch (ParseException e) {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                PrintWriter pw = response.getWriter();
                pw.write("{\"result\" : \"ng\"}");
                pw.flush();
                return null;
            }
            task.setDue(due);
        }
        TasksUtil.tasksUpdate(access_token, tasklist, task);

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter pw = response.getWriter();
        pw.write("{\"result\" : \"ok\"}");
        pw.flush();

        return null;
    }
}
