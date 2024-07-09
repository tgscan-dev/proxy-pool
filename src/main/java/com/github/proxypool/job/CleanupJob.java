package com.github.proxypool.job;

import com.github.proxypool.repository.ProxyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CleanupJob {

  @Autowired private ProxyRepository proxyRepository;

  @Scheduled(fixedRate = 1000 * 60 * 20)
  public void refresh() {
    log.info("Cleanup job started");
    var row = proxyRepository.deleteByFailCountGreaterThan(5);
    log.info("Deleted {} proxies", row);
  }
}
