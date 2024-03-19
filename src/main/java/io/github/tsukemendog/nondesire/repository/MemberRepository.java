package io.github.tsukemendog.nondesire.repository;

import io.github.tsukemendog.nondesire.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {
    @EntityGraph(attributePaths = "progresses.challengeLevel")
    Optional<Member> findByServiceIdAndProviderAndIsActive(String serviceId, String provider, Boolean isActive);

    @EntityGraph(attributePaths = "diaries")
    List<Member> findAllByIsActive(Boolean isActive);

}
