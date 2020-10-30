package cn.water.learncom.controller;

import cn.water.learncom.dto.AccessTokenDTO;
import cn.water.learncom.dto.GithubUser;
import cn.water.learncom.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirect_Uri);
        accessTokenDTO.setClient_id(client_Id);
        accessTokenDTO.setClient_secret(client_Secret);
//        accessTokenDTO.setRedirect_uri("http://localhost:8887/callback");
//        accessTokenDTO.setClient_id("948161471acd2ac79b1a");
//        accessTokenDTO.setClient_secret("b5c3d2d8ee18485e0e935088d51c1648a6659ca7");
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser user = githubProvider.getUser(accessToken);
        System.out.println(user.getName());
        return "index";
    }
}
