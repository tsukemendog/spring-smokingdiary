package io.github.tsukemendog.nondesire.repository;

import io.github.tsukemendog.nondesire.entity.Progress;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProgressRepository extends CrudRepository<Progress, Long> {
    @Modifying
    @Query("DELETE FROM Progress p WHERE p.id IN (:ids)")
    void deleteAllByIdIn(List<Long> ids);
}
