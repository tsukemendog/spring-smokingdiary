package io.github.tsukemendog.nondesire;


import io.github.tsukemendog.nondesire.entity.Daily;
import io.github.tsukemendog.nondesire.entity.Member;
import io.github.tsukemendog.nondesire.repository.MemberRepository;
import io.github.tsukemendog.nondesire.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class ScheduleConfig {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    @Transactional
    @Scheduled(cron = "1 0 0 * * *") // 자정마다 실행
    //@Scheduled(fixedDelay = 60000) // 자정마다 실행
    public void performTask() {
        System.out.println("정기적으로 실행되는 작업");
        List<Member> members = memberRepository.findAllByIsActive(true);

        members.forEach(member -> {
            List<Daily> diaries = member.getDiaries();
            if (diaries.isEmpty()) {
                return;
            }
            Daily recentDiary = diaries.get(diaries.size() - 1);
            if (!checkSincerityDiary(recentDiary.getRegDate())) {
                member.getDiaries().forEach(diary -> {
                    diary.setIsActive(false);
                });

                memberService.giveUp(member);
            }
        });
    }


    public boolean checkSincerityDiary(LocalDateTime previousRegDate) {
        LocalDateTime now = LocalDateTime.now();

        String postedRegDateStr = "" + previousRegDate.getYear() + previousRegDate.getMonth().getValue() + previousRegDate.getDayOfMonth();
        String nowRegDateStr = "" + now.getYear() + now.getMonth().getValue() + now.getDayOfMonth();

        Integer postedRegDate = Integer.parseInt(postedRegDateStr);
        Integer nowRegDate = Integer.parseInt(nowRegDateStr);

        return (nowRegDate - postedRegDate) <= 1;
    }
}
