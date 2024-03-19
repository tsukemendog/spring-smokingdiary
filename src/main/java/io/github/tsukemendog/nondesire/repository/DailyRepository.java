package io.github.tsukemendog.nondesire.repository;

import io.github.tsukemendog.nondesire.entity.Daily;
import io.github.tsukemendog.nondesire.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DailyRepository extends CrudRepository<Daily, Long> {
    @EntityGraph(attributePaths = "member")
    List<Daily> findByMemberAndIsActive(Member member, Boolean isActive);

    @EntityGraph(attributePaths = "member")
    Optional<Daily> findFirstByMemberAndIsActiveOrderByDaysDesc(Member member, Boolean isActive);

    Optional<Daily> findByIdAndMemberAndIsActive(Long id, Member member, Boolean isActive);
}
