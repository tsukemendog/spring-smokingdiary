package io.github.tsukemendog.nondesire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tsukemendog.nondesire.entity.Member;
import io.github.tsukemendog.nondesire.service.DailyService;
import io.github.tsukemendog.nondesire.service.IPLogsService;
import io.github.tsukemendog.nondesire.service.MemberService;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import io.github.tsukemendog.nondesire.enums.Level;

@Controller
public class MainController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private DailyService dailyService;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    @Autowired
    private Environment environment;
    @Autowired
    private IPLogsService ipLogsService;
    
    @Value(value = "${current-domain}")
	private String currentDomain;


    @GetMapping
    public String home(Model model, @AuthenticationPrincipal OAuth2User oAuth2User, OAuth2AuthenticationToken token, HttpServletRequest request) {
        //System.out.println(principal.getAttributes());
        if (request.getSession().getAttribute("isFirst") != null) {
            //System.out.println("isFirst : " + request.getSession().getAttribute("isFirst"));
            request.getSession().removeAttribute("isFirst");
        }

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }

        ipLogsService.save(ipAddress);

        if (oAuth2User == null) {
            model.addAttribute("isStarted", false);
        } else {
            Member member = memberService.getMemberByOAuth2UserAndToken(oAuth2User, token);
            MemberService.MemberDto memberDto = memberService.convertToMemberDto(member);

            model.addAttribute("isStarted", memberDto.getStartTime() != null);
            model.addAttribute("level", memberDto.getLevel());
        }

        model.addAttribute("message", "Hello Spring Boot and Thymeleaf!");
    
        return "home";
    }


    @GetMapping("/timer")
    public String timer(Model model, @AuthenticationPrincipal OAuth2User principal) {
        return "timer";
    }

    @GetMapping("/oauth2-login")
    public String test(Model model, @AuthenticationPrincipal OAuth2User principal) {
        return "login";
    }


    @PostMapping
    public String giveUp(@AuthenticationPrincipal OAuth2User oAuth2User, OAuth2AuthenticationToken token, Model model) {
        Member member = memberService.getMemberByOAuth2UserAndToken(oAuth2User, token);
        memberService.giveUp(member);

        model.addAttribute("isStarted", false);
        model.addAttribute("level", 0L);
        return "home";
    }


    @GetMapping("/my-page")
    public String mypage(@AuthenticationPrincipal OAuth2User oAuth2User, OAuth2AuthenticationToken token, Model model) {
        Member member = memberService.getMemberByOAuth2UserAndToken(oAuth2User, token);
        MemberService.MemberDto memberDto = memberService.convertToMemberDto(member);

        model.addAttribute("image", currentDomain + memberDto.getImage());
        model.addAttribute("title", memberDto.getTitle());
        model.addAttribute("message", memberDto.getMessage());
        return "mypage";
    }

    @PostMapping("/my-page/leave")
    public String leave(@AuthenticationPrincipal OAuth2User oAuth2User, OAuth2AuthenticationToken token) throws Exception {

        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        token.getAuthorizedClientRegistrationId(),
                        token.getName());

        // 리프레시 토큰은 모든 OAuth2 클라이언트 타입에서 사용 가능한 것은 아닙니다
        String refreshToken = null;
        if (client.getRefreshToken() != null) {
            refreshToken = client.getRefreshToken().getTokenValue();

            String provider = token.getAuthorizedClientRegistrationId();
            if ("naver".equals(provider)) {
                Map<String, String> refreshResult = requestToNaver(refreshToken, false);
                String accessToken = refreshResult.get("access_token");
                String refresh = refreshResult.get("refresh_token");

                System.out.println("naver acc : " + accessToken);
                System.out.println("naver ref : " + refresh);

                Map<String, String> deleteResult = requestToNaver(accessToken, true);
                String result = deleteResult.get("result");
                System.out.println("result : " + deleteResult.get("result"));

            } else {
                Map<String, String> refreshResult = (Map) requestToKakao(refreshToken, false);
                String accessToken = refreshResult.get("access_token");
                String refresh = refreshResult.get("refresh_token");

                System.out.println("kakao acc : " + accessToken);
                System.out.println("kakao ref : " + refresh);

                Map<String, Object> deleteResult = requestToKakao(accessToken, true);
                System.out.println("kakao deleted id : " + deleteResult.get("id"));

            }


            Member member = memberService.getMemberByOAuth2UserAndToken(oAuth2User, token);
            memberService.giveUp(member);
            dailyService.resetDiary(oAuth2User, token);
            memberService.inActiveMember(member);
            memberService.clearProcesses(member);

            return "redirect:/logout";
        }


        return "redirect:/";
    }



    public Map<String, String> requestToNaver(String token, boolean isDeleted) throws Exception {

        // HttpClient 인스턴스 생성
        HttpClient client = HttpClient.newHttpClient();

        String clientId = environment.getProperty("spring.security.oauth2.client.registration.naver.client-id");
        String clientSecret = environment.getProperty("spring.security.oauth2.client.registration.naver.clientSecret");

        // 폼 데이터 준비
        Map<Object, Object> data = new HashMap<>();
        data.put("client_id", clientId);
        data.put("client_secret", clientSecret);
        if (isDeleted) {
            data.put("access_token", token);
            data.put("service_provider", "NAVER");
            data.put("grant_type", "delete");
        } else {
            data.put("refresh_token", token);
            data.put("grant_type", "refresh_token");
        }


        // 폼 데이터를 URL 인코딩된 문자열로 변환
        String form = buildFormData(data);


        // POST 요청 생성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://nid.naver.com/oauth2.0/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        // 요청 전송 및 응답 수신
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("http res : " + response.body());
        ObjectMapper mapper = new ObjectMapper();

        // JSON 문자열을 Map으로 변환

        return mapper.readValue(response.body(), Map.class);
    }

    public Map<String, Object> requestToKakao(String token, boolean isDeleted) throws Exception {

        // HttpClient 인스턴스 생성
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request;

        String clientId = environment.getProperty("spring.security.oauth2.client.registration.kakao.client-id");
        String clientSecret = environment.getProperty("spring.security.oauth2.client.registration.kakao.client-secret");


        if (isDeleted) {
            request = HttpRequest.newBuilder()
                    .uri(URI.create("https://kapi.kakao.com/v1/user/unlink"))
                    .header("Authorization", "Bearer " + token)
                    .GET() // GET 메소드 사용
                    .build();
        } else {

            // 폼 데이터 준비
            Map<Object, Object> data = new HashMap<>();
            data.put("client_id", clientId);
            data.put("client_secret", clientSecret);
            data.put("refresh_token", token);
            data.put("grant_type", "refresh_token");

            // 폼 데이터를 URL 인코딩된 문자열로 변환
            String form = buildFormData(data);

            // POST 요청 생성
            request = HttpRequest.newBuilder()
                    .uri(URI.create("https://kauth.kakao.com/oauth/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .build();
        }

        // 요청 전송 및 응답 수신
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("http res : " + response.body());
        ObjectMapper mapper = new ObjectMapper();

        // JSON 문자열을 Map으로 변환

        return mapper.readValue(response.body(), Map.class);
    }

    // 폼 데이터를 URL 인코딩된 문자열로 변환하는 메소드
    private static String buildFormData(Map<Object, Object> data) {
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            sj.add(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8) + "="
                    + URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return sj.toString();
    }
}
