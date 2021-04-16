package com.example.technologyforum.config;

import com.example.technologyforum.web.pojo.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 全局拦截器
 *
 * @author Monster
 * @since 1.0.0-SNAPSHOT
 */
@Component
public class GlobalInterceptor extends HandlerInterceptorAdapter {

  /**
   * 前置处理器
   *
   * @param request
   * @param response
   * @param handler
   * @return
   * @throws Exception
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String uri = request.getRequestURI();
    // 主页放过
    if ("/".equals(uri)) {
       // request.getRequestDispatcher("/front/index").forward(request, response);
        return true;
    }
    String[] pre = uri.split("/");
    //uri.contains("front")
      //        ||uri.contains("comm")
    if ("front".equals(pre[1])
        ||"comm".equals(pre[1])
        || uri.contains("login")
        || uri.contains("register")
        || uri.contains("email-send")
        || uri.contains("forgetpass")
        || uri.contains("logout")) {
      return true;
    }
    HttpSession session = request.getSession();
    User sysUser = (User) session.getAttribute("userinfo");
    if (sysUser != null) {
      return true;
    }
    if("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))){
      //告诉ajax我是重定向
      response.setHeader("REDIRECT", "REDIRECT");
      //告诉ajax我重定向的路径
      response.setHeader("CONTENTPATH", "/front/login");
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }else{
      request.getRequestDispatcher("/front/login").forward(request, response);
    }
    return false;
  }
}
