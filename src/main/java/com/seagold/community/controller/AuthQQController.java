/**
 * FileName: AuthQQController
 * Author:   xjh
 * Date:     2020-02-13 13:53
 * Description: QQ授权登陆
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.seagold.community.controller;

import com.alibaba.fastjson.JSON;
import com.seagold.community.dto.QQUser;
import com.seagold.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈QQ授权登陆〉
 *
 * @author xjh
 * @create 2020-02-13
 * @since 1.0.0
 */
@Slf4j
@Controller
@RequestMapping("/qq")
public class AuthQQController {

    @Autowired
    private UserService userService;

    @Value("${qq.app.id}")
    private String appId;
    @Value("${qq.app.key}")
    private String appKey;
    @Value("${qq.app.redirectUri}")
    private String redirectUri;

    @RequestMapping("/render")
    public void renderAuth(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getAuthRequest();
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @RequestMapping("/callback")
    public String login(AuthCallback callback,
                        HttpServletResponse response,
                        HttpSession session) {
        AuthRequest authRequest = getAuthRequest();
        AuthResponse login = authRequest.login(callback);
        String qqUser  = JSON.toJSON(login.getData()).toString();
        QQUser user = JSON.parseObject(qqUser, QQUser.class);
        int code = userService.qqUser(user, response, session);
        if (200 == code){
            return "redirect:/";
        }
        session.setAttribute("message", "QQ授权登陆失败请重试");
        return "error";
    }


    private AuthRequest getAuthRequest() {
        return new AuthQqRequest(AuthConfig.builder()
                .clientId(appId)
                .clientSecret(appKey)
                .redirectUri(redirectUri)
                .build());
    }
}