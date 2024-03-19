package io.github.tsukemendog.nondesire.service;

import io.github.tsukemendog.nondesire.entity.IPLogs;
import io.github.tsukemendog.nondesire.repository.IPLogsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class IPLogsService {

    private final IPLogsRepository ipLogsRepository;

    @Transactional
    public void save(String ipv4) {
        ipLogsRepository.save(IPLogs.builder()
                        .ipv4(ipv4)
                        .regDate(LocalDateTime.now())
                .build());
    }
}
