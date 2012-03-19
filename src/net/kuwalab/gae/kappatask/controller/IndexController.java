package net.kuwalab.gae.kappatask.controller;

import net.kuwalab.google.util.TasksUtil;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class IndexController extends Controller {

    @Override
    public Navigation run() throws Exception {
        requestScope("auth_url", TasksUtil.getOAuthAddress());

        return forward("index.jsp");
    }
}
