package io.github.tsukemendog.nondesire.controller;

import io.github.tsukemendog.nondesire.entity.Member;
import io.github.tsukemendog.nondesire.entity.Progress;
import io.github.tsukemendog.nondesire.service.LevelService;
import io.github.tsukemendog.nondesire.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MeasurementController {

    private final MemberService memberService;
    private final LevelService levelService;


    @PostMapping("/level")
    public ResponseEntity<Map<String, Object>> measurement(@AuthenticationPrincipal OAuth2User oAuth2User, OAuth2AuthenticationToken token) {

        Map<String, Object> resultMap = new HashMap<>();
        
        Member member = memberService.getMemberByOAuth2UserAndToken(oAuth2User, token);
        MemberService.MemberDto memberDto = memberService.convertToMemberDto(member);

        resultMap.put("level", 1L);
        resultMap.put("levelMessage", "현재 레벨 1입니다. 다음 레벨까지 남은시간..");
        resultMap.put("message", "");

        if (memberDto.getLevel() <= 0) {
            Progress newProgress = levelService.updateLevel(member);

            return ResponseEntity.ok(resultMap);
        }

        if (memberDto.getStartTime() == null) {

            return ResponseEntity.ok(resultMap);
        }

        Boolean isAvailableNextLevel = levelService.checkRemainingTimeForNextLevel(memberDto.getStartTime(), memberDto.getDuration());

        resultMap.put("level", memberDto.getLevel());
        resultMap.put("levelMessage", "현재 레벨 " + memberDto.getLevel() + "입니다. 다음 레벨까지 남은시간..");


        if (isAvailableNextLevel) {

            long currentMilli = LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();

            long preDuration = memberDto.getDuration() - (currentMilli - memberDto.getEpochStartTime());

            MemberService.MemberDto newMemberDto = MemberService.MemberDto.builder().build();
            while (preDuration < 0L) {
                System.out.println("preDuration : " + preDuration);
                Progress newProgress = levelService.updateLevel(member);
                member.getProgresses().add(newProgress);
                newMemberDto = memberService.convertToMemberDto(member);

                preDuration = newMemberDto.getDuration() - (currentMilli - newMemberDto.getEpochStartTime());
            }



            long newLevel = newMemberDto.getLevel();
            resultMap.put("level", newLevel);
            resultMap.put("levelMessage", "현재 레벨 " + newLevel + "입니다. 다음 레벨까지 남은시간..");
            resultMap.put("duration", newMemberDto.getDuration() - (currentMilli - newMemberDto.getEpochStartTime()));
            return ResponseEntity.ok(resultMap);
        }

        resultMap.put("message", "잘못된 요청입니다.");
        return ResponseEntity.ok(resultMap);
    }

    //메인화면에 보여질 남은시간 ( 로그인 하지않으면 레벨1 에서 레벨업을 위한 Duration 을 표시 )
    @GetMapping("/remain-time")
    public ResponseEntity<Map<String, Object>> getRemainTime(@AuthenticationPrincipal OAuth2User oAuth2User, OAuth2AuthenticationToken token) {
        Map<String, Object> resultMap = new HashMap<>();

        if (oAuth2User == null) {
            resultMap.put("isStarted", false);
            resultMap.put("duration",levelService.getInitLevelDuration().toString());
            return ResponseEntity.ok(resultMap);
        }

        Member member = memberService.getMemberByOAuth2UserAndToken(oAuth2User, token);
        MemberService.MemberDto memberDto = memberService.convertToMemberDto(member);

        long currentLevelEndTime =  memberDto.getEpochStartTime() + memberDto.getDuration();
        long currentTime = memberDto.getEpochCurrentTime();

        long remainTime = memberDto.getEpochStartTime() <= 0 ? memberDto.getDuration() : currentLevelEndTime - currentTime;

        resultMap.put("isStarted", memberDto.getStartTime() != null);
        resultMap.put("duration",Long.toString(remainTime));
        return ResponseEntity.ok(resultMap);
    }

    @PostMapping("/start")
    public ResponseEntity<String> start(@AuthenticationPrincipal OAuth2User oAuth2User, OAuth2AuthenticationToken token) {
        Member member = memberService.getMemberByOAuth2UserAndToken(oAuth2User, token);
        if (member == null) {
            return ResponseEntity.status(400).body("멤버 조회불가");
        }

        if (member.getStartTime() != null) {
            return ResponseEntity.status(400).body("잘못된 요청입니다.");
        }

        memberService.start(member);
        MemberService.MemberDto memberDto = memberService.convertToMemberDto(member);

        return ResponseEntity.ok(Long.toString(memberDto.getDuration()));
    }

}
