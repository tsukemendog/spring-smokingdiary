package io.github.tsukemendog.nondesire.service;

import io.github.tsukemendog.nondesire.entity.ChallengeLevel;
import io.github.tsukemendog.nondesire.entity.Member;
import io.github.tsukemendog.nondesire.entity.Progress;
import io.github.tsukemendog.nondesire.entity.ProgressLog;
import io.github.tsukemendog.nondesire.repository.ChallengeLevelRepository;
import io.github.tsukemendog.nondesire.repository.MemberRepository;
import io.github.tsukemendog.nondesire.repository.ProgressLogRepository;
import io.github.tsukemendog.nondesire.repository.ProgressRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ProgressRepository progressRepository;
    private final ChallengeLevelRepository challengeLevelRepository;
    private final ProgressLogRepository progressLogRepository;

    @Transactional(readOnly = true)
    public Member getMemberByOAuth2UserAndToken(OAuth2User oAuth2User, OAuth2AuthenticationToken token) {
        Map<String, String> oauthMap = getServiceIdAndProvider(oAuth2User, token);
        String id = oauthMap.get("id");
        String provider = oauthMap.get("provider");
        Optional<Member> memberOptional = memberRepository.findByServiceIdAndProviderAndIsActive(id, provider, true);
        return memberOptional.orElse(null);
    }

    public Map<String, String> getServiceIdAndProvider(OAuth2User oAuth2User, OAuth2AuthenticationToken token) {
        String provider = token.getAuthorizedClientRegistrationId();
        Map<String, Object> oAuth2UserMap = oAuth2User.getAttributes();

        String id;
        if ("naver".equals(provider)) {
            Map<String, String> response = (Map<String, String>) oAuth2UserMap.get("response");
            id = response.get("id");
        } else {
            id = ((Long) oAuth2UserMap.get("id")).toString();
        }

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("id", id);
        resultMap.put("provider", provider);
        return resultMap;
    }

    @Transactional
    public void start(Member member) {
        LocalDateTime now = LocalDateTime.now();

        //사용자 시간측정시작!
        member.setStartTime(now);
        progressLogRepository.save(ProgressLog.builder()
                        .logTime(now)
                        .isStart(true)
                        .latestLevel(1L)
                        .member(member)
                .build());
    }
    @Transactional
    public void inActiveMember(Member member) {
        member.setIsActive(false);
    }

    @Transactional
    public void giveUp(Member member) {
        Map<Boolean, List<Progress>> partitionMap = member.getProgresses()
                .stream().collect(Collectors.partitioningBy(obj -> obj.getChallengeLevel().getLevelNumber() == 0L));

        Progress levelZero = partitionMap.get(true).get(0);
        List<Progress> progresses = partitionMap.get(false);

        levelZero.setCompleteTime(null);
        progressRepository.deleteAll(progresses);  //1번 삭제

        member.setStartTime(null);

        MemberDto memberDto = convertToMemberDto(member);

        progressLogRepository.save(ProgressLog.builder()
                .logTime(LocalDateTime.now())
                        .latestLevel(memberDto.getLevel())
                .isStart(false)
                .member(member)
                .build());
    }

    @Transactional
    public void clearProcesses(Member member) {
        progressRepository.deleteAll(member.getProgresses());
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class MemberDto {
        private String serviceId;
        private String provider;
        private Long level;
        private LocalDateTime startTime;
        private Long epochStartTime;
        private Long epochCurrentTime;
        private Long duration;
        private String image;
        private String title;
        private String message;
    }

    public MemberDto convertToMemberDto (Member member) {
        List<Progress> progresses = member.getProgresses();
        Progress findProgress = Collections.max(progresses,
                Comparator.comparing(progress -> progress.getChallengeLevel().getLevelNumber()));

        System.out.println("현재 레벨 : " + findProgress.getChallengeLevel().getLevelNumber());

        //LocalDateTime nextLevelLocalDateTime = Instant.ofEpochMilli(epochNextLevelMilliSecond).atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        return MemberDto.builder()
                .serviceId(member.getServiceId())
                .provider(member.getProvider())
                .level(findProgress.getChallengeLevel().getLevelNumber())
                .duration(findProgress.getChallengeLevel().getDuration())
                .startTime(member.getStartTime())
                .epochStartTime(member.getStartTime() != null ? member.getStartTime().atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli() : 0L)
                .epochCurrentTime(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli())
                .image(findProgress.getChallengeLevel().getImage())
                .title(findProgress.getChallengeLevel().getTitle())
                .message(findProgress.getChallengeLevel().getMessage())
                .build();
    }
}
