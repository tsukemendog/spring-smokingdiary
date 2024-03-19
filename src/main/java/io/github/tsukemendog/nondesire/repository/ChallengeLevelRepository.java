package io.github.tsukemendog.nondesire.repository;

import io.github.tsukemendog.nondesire.entity.ChallengeLevel;
import org.springframework.data.repository.CrudRepository;

public interface ChallengeLevelRepository extends CrudRepository<ChallengeLevel, Long> {
    ChallengeLevel findByLevelNumber(Long levelNumber);
}
