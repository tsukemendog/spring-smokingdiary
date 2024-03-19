package io.github.tsukemendog.nondesire.service;

import io.github.tsukemendog.nondesire.entity.ChallengeLevel;
import io.github.tsukemendog.nondesire.entity.Member;
import io.github.tsukemendog.nondesire.entity.Progress;
import io.github.tsukemendog.nondesire.repository.ChallengeLevelRepository;
import io.github.tsukemendog.nondesire.repository.ProgressRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class LevelService {

    private final ChallengeLevelRepository challengeLevelRepository;
    private final ProgressRepository progressRepository;
    public Boolean checkRemainingTimeForNextLevel(LocalDateTime startTimeByCurrentLevel, Long durationByGoal) {
        LocalDateTime now = LocalDateTime.now();
        Long durationByCurrent = Duration.between(startTimeByCurrentLevel, now).toMillis();


        return durationByGoal < durationByCurrent;
    }

    @Transactional
    public Progress updateLevel(Member member) {

        List<Progress> progresses = member.getProgresses();
        Progress findProgress = Collections.max(progresses,
                Comparator.comparing(progress -> progress.getChallengeLevel().getLevelNumber()));

        Long nextLevelNumber = findProgress.getChallengeLevel().getLevelNumber() + 1L;
        ChallengeLevel nextLevel = challengeLevelRepository.findByLevelNumber(nextLevelNumber);

        LocalDateTime now = LocalDateTime.now();

        findProgress.setCompleteTime(now);

        return progressRepository.save(Progress.builder()
                        .completeTime(null)
                        .member(member)
                        .challengeLevel(nextLevel)
                .build());
    }

    @Transactional(readOnly = true)
    public Long getInitLevelDuration() {
        return challengeLevelRepository.findByLevelNumber(1L).getDuration();
    }
}
