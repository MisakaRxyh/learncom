package cn.water.learncom.provider;



import cn.water.learncom.dto.AccessTokenDTO;
import cn.water.learncom.dto.GithubUser;
import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String url = "https://github.com/login/oauth/access_token";

        RequestBody body = RequestBody.create(JSON.toJSONString(accessTokenDTO), mediaType);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            // string = access_token=312f0bed61c9324ed7598c7e81e94b1b0daa7745&expires_in=28800&...
            String[] split = string.split("&");
            String token = split[0].split("=")[1];
            System.out.println(token);
            return token;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.github.com/user";

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization","token " + accessToken)
                //token后面有个空格 不能省略
                //官方推荐的使用access_token安全访问API的方式，使用Github推荐的最新方式（Authorization HTTP header）
                //https://niter.cn/p/115
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            System.out.println(string);
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
        }
        return null;
    }
}
