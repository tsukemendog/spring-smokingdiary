package io.github.tsukemendog.nondesire.repository;

import io.github.tsukemendog.nondesire.entity.Member;
import io.github.tsukemendog.nondesire.entity.ProgressLog;
import org.springframework.data.repository.CrudRepository;

public interface ProgressLogRepository extends CrudRepository<ProgressLog, Long> {

}
