package io.github.tsukemendog.nondesire.security;

import io.github.tsukemendog.nondesire.entity.ChallengeLevel;
import io.github.tsukemendog.nondesire.entity.Member;
import io.github.tsukemendog.nondesire.entity.Progress;
import io.github.tsukemendog.nondesire.repository.ChallengeLevelRepository;
import io.github.tsukemendog.nondesire.repository.DailyRepository;
import io.github.tsukemendog.nondesire.repository.ProgressRepository;
import io.github.tsukemendog.nondesire.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final ChallengeLevelRepository challengeLevelRepository;
    private final ProgressRepository progressRepository;
    private final DailyRepository dailyRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            OAuth2User user = oauthToken.getPrincipal();
            String provider = oauthToken.getAuthorizedClientRegistrationId();
            Map<String, Object> attributes = user.getAttributes();

            String id;
            //String name;
            //String profileImage;


            switch (provider) {
                case "naver" :
                    Map<String, String> responseMap = (Map<String, String>) attributes.get("response");
                    id = responseMap.get("id");
                    //name = responseMap.get("name");
                    //profileImage = responseMap.get("profile_image");


                    break;

                case "kakao" :
                    Map<String, String> propertiesMap = (Map<String, String>) attributes.get("properties");
                    id = ((Long) attributes.get("id")).toString();
                    //name = propertiesMap.get("nickname");
                    //profileImage = propertiesMap.get("profile_image");


                    break;
                default:
                    id = "";
                    //name = "";
                    //profileImage = "";
            }

            Optional<Member> optionalMember = memberRepository.findByServiceIdAndProviderAndIsActive(id, provider, true);
            optionalMember.ifPresentOrElse(
                    member -> {  //멤버가 존재하는 경우

                    },

                    () -> {

                        ChallengeLevel challengeLevel = challengeLevelRepository.findByLevelNumber(0L); //최초 1레벨 시작
                        Member member = memberRepository.save(Member.builder() //멤버 최초가입
                                .serviceId(id)

                                .provider(provider)

                                .isActive(true)
                                .regDate(LocalDateTime.now())
                                .build());

                        progressRepository.save(Progress.builder()  //레벨 진행상황 입력

                                        .member(member)
                                        .challengeLevel(challengeLevel)
                                .build());


                        /* 테스트 일기 데이터 */
                        /*List<Daily> diaries = new ArrayList<>();
                        for (int i=1; i<=10; i++) {
                            diaries.add(Daily.builder()
                                    .title("test" + i)
                                    .content("content " + i)
                                    .isActive(true)
                                    .days(i)
                                    .regDate(LocalDateTime.of(2024, 1, 15 + i, 1, 30))
                                    .member(member)
                                    .build());
                        }
                        dailyRepository.saveAll(diaries);*/

                        request.getSession().setAttribute("isFirst", true);  //최초 가입인증 여부
                    });


            response.sendRedirect("/");
        }
    }
}
