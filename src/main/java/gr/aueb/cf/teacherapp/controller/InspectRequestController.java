package gr.aueb.cf.teacherapp.controller;

import gr.aueb.cf.teacherapp.core.enums.Role;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Enumeration;

@WebServlet("/inspect-request")
public class InspectRequestController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String username = request.getParameter("username");     // never send username as query param
        String password = request.getParameter("password");     // never send password as query param
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        // Inspecting session id
        response.getWriter().write("Session Id: " + request.getSession().getId());
        System.out.println("Session Id: " + request.getSession().getId());

        // Inspecting request headers
        response.getWriter().write("\n\nHeaders\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println(headerName + ": " + headerValue);
            response.getWriter().write(headerName + ": " + headerValue + "\n");
        }

        // Inspecting request cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    System.out.println("Cookie Name: " + cookie.getName() + ", Cookie Value: " + cookie.getValue());
                    response.getWriter().write("Cookie Name: " + cookie.getName() + ", Cookie Value: " + cookie.getValue() + "\n");
                }
            }
        }

        // Inspecting session id (JSESSIONID)
        //HttpSession session = request.getSession(true);

        response.getWriter().write("\n\nNew Session Id\n"); // After login to tackle Session Fixation
        HttpSession oldSession = request.getSession(false); // Get current session (if exists) else null
        if (oldSession != null) {
            oldSession.invalidate(); // Destroy the old session
        }
        HttpSession newSession = request.getSession(true);  // Get current session (if exists) else create new session
        String sessionId = newSession.getId();

        request.getSession().setAttribute("username", username);
        String sessionUsername = (String) request.getSession().getAttribute("username");
        System.out.println("username: " + sessionUsername);
        response.getWriter().write("username: " + sessionUsername + "\n");

        if (username != null && username.equals("thanassis") && password.equals("12345")) {
            newSession.setAttribute("role", Role.TEACHER.name());
        } else {
            newSession.setAttribute("role", Role.STUDENT.name());
        }

        System.out.println("New Session ID: " + sessionId);
        System.out.println("User in session role: " + newSession.getAttribute("role"));
        response.getWriter().write("New Session ID: " + sessionId + "\n");

        // Inspecting session attributes


        // Inspecting request URI and context path
        response.getWriter().write("\n\nRequest URI and Context Path\n");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("Servlet Path: " + request.getServletPath());
        response.getWriter().write("Request URI: " + request.getRequestURI() + "\n");
        response.getWriter().write("Context Path: " + request.getContextPath() + "\n");
        response.getWriter().write("Servlet Path: " + request.getServletPath() + "\n");

        response.setContentType("text/plain");
        response.getWriter().write("Request inspection completed. Check server logs for details.");

        //request.getRequestDispatcher("/school-app/teachers").forward(request, response);
        response.sendRedirect("/school-app/teachers");
    }
}
