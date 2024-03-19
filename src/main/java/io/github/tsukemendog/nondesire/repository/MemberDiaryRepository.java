package io.github.tsukemendog.nondesire.repository;

import io.github.tsukemendog.nondesire.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MemberDiaryRepository extends CrudRepository<Member, Long> {
    @EntityGraph(attributePaths = "diaries")
    Optional<Member> findByServiceIdAndProviderAndIsActive(String serviceId, String provider, Boolean isActive);
}
