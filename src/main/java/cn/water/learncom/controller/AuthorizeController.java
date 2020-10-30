package cn.water.learncom.controller;

import cn.water.learncom.dto.AccessTokenDTO;
import cn.water.learncom.dto.GithubUser;
import cn.water.learncom.mapper.UserMapper;
import cn.water.learncom.model.User;
import cn.water.learncom.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String client_Id;
    @Value("${github.client.secret}")
    private String client_Secret;
    @Value("${github.redirect.uri}")
    private String redirect_Uri;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirect_Uri);
        accessTokenDTO.setClient_id(client_Id);
        accessTokenDTO.setClient_secret(client_Secret);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if (githubUser != null){
            //登录成功，写入cookie 和 session
            System.out.println(githubUser.getName());
            User user = new User();
            user.setAccountId(String.valueOf(githubUser.getId()));
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            response.addCookie(new Cookie("token", token));
            request.getSession().setAttribute("user",user);
            return "redirect:/";
        }
        else {
            //登录失败，重新登录
        }
        return "redirect:/";
    }
}
