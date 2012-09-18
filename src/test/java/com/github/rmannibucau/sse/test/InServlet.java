package com.github.rmannibucau.sse.test;

import com.github.rmannibucau.sse.SSESender;
import com.github.rmannibucau.sse.cdi.SSE;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/in")
public class InServlet extends HttpServlet {
    @Inject
    @SSE("/impl")
    private SSESender sse;

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        sse.sendMessage("In message\r\n");

        resp.getWriter().write("ok");
    }
}
