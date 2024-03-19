package io.github.tsukemendog.nondesire.service;

import io.github.tsukemendog.nondesire.entity.Daily;
import io.github.tsukemendog.nondesire.entity.Member;
import io.github.tsukemendog.nondesire.repository.DailyRepository;
import io.github.tsukemendog.nondesire.repository.MemberDiaryRepository;
import lombok.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DailyService {

    private final DailyRepository dailyRepository;
    private final MemberService memberService;
    private final MemberDiaryRepository memberDiaryRepository;
    public final static int limitDay = 66;
    @Transactional(readOnly = true)
    public Daily getRecentDaily(Member member) {
        return dailyRepository.findFirstByMemberAndIsActiveOrderByDaysDesc(member, true).orElse(null);
    }

    @Transactional(readOnly = true)
    public Optional<Daily> findByIdAndMember(Long id, Member member) {
        return dailyRepository.findByIdAndMemberAndIsActive(id, member, true);
    }

    @Transactional
    public void save(Member member, Daily daily, Map<String, String> request) {
        dailyRepository.save(Daily.builder()
                        .title(request.get("title"))
                        .content(request.get("content"))
                        .isActive(true)
                        .days(daily.getDays() + 1)
                        .regDate(LocalDateTime.now())
                        .member(member)
                .build());
    }

    public List<List<DailyDay>> getTotalDays() {
        List<List<DailyDay>> list = new ArrayList<>();

        for (int i = 11; i <= limitDay; i = i + 11) {
            List<DailyDay> days = new ArrayList<>();
            for (int j = i - 10; j <= i; j++) {
                days.add(DailyDay.builder()
                        .day(j)
                        .isCompleted(false)
                        .build());
            }
            list.add(days);
        }

        return list;
    }

    @Transactional
    public void resetDiary(OAuth2User oAuth2User, OAuth2AuthenticationToken token) {
        Map<String, String> oauthMap = memberService.getServiceIdAndProvider(oAuth2User, token);
        String id = oauthMap.get("id");
        String provider = oauthMap.get("provider");
        Optional<Member> memberOptional = memberDiaryRepository.findByServiceIdAndProviderAndIsActive(id, provider, true);
        memberOptional.ifPresent(member -> {
            member.getDiaries().forEach(diary -> {
                diary.setIsActive(false);
            });
        });

        Member member = memberService.getMemberByOAuth2UserAndToken(oAuth2User, token);
        memberService.giveUp(member);
    }

    @Transactional(readOnly = true)
    public List<List<DailyDay>> getCheckedTotalDays(Member member) {
        List<List<DailyDay>> totalDays = getTotalDays();
        List<Daily> dailies = dailyRepository.findByMemberAndIsActive(member, true);
        totalDays.forEach(list ->
            list.forEach(el -> {
                Optional<Daily> optionalDaily = dailies.stream().filter(daily -> daily.getDays().equals(el.getDay())).findFirst();
                if (optionalDaily.isPresent()) {
                    Daily daily = optionalDaily.get();
                    if (daily.getDays().equals(el.getDay())) {
                        el.setIsCompleted(true);
                        el.setId(daily.getId());
                    }
                }

                /*if (dailies.stream().anyMatch(daily -> daily.getDays().equals(el.getDay()))) {
                    el.setIsCompleted(true);

                }*/
            })
        );

        return totalDays;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class DailyDay {
        private Integer day;
        private Boolean isCompleted;
        private Long id;
    }


    public boolean checkDuplicateDiary(LocalDateTime recentRegDate) {
        LocalDateTime now = LocalDateTime.now();

        String recentRegDateStr = "" + recentRegDate.getYear() + recentRegDate.getMonth().getValue() + recentRegDate.getDayOfMonth();
        String nowRegDateStr = "" + now.getYear() + now.getMonth().getValue() + now.getDayOfMonth();

        Integer recentInt = Integer.parseInt(recentRegDateStr);
        Integer nowInt = Integer.parseInt(nowRegDateStr);

        return (nowInt - recentInt) != 0;
    }
}
