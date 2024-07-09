package com.github.proxypool.job;

import com.github.proxypool.domain.Proxy;
import com.github.proxypool.dto.ProxyDto;
import com.github.proxypool.repository.ProxyRepository;
import com.github.proxypool.service.ProxyChecker;
import com.github.proxypool.service.ProxyFetcher;
import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScrapeJob {

  @Autowired private ProxyFetcher proxyFetcher;

  @Autowired private ProxyChecker proxyChecker;
  @Autowired private ProxyRepository proxyRepository;

  @Scheduled(fixedRate = 1000 * 3)
  public void scrape() {
    log.info("Scrape job started");
    var proxies = proxyFetcher.getHttpProxies();
    for (List<String> proxies0 : Lists.partition(Lists.newArrayList(proxies), 500)) {
      var checked =
          proxyChecker.check(new HashSet<>(proxies0)).stream()
              .map(ProxyDto::toProxy)
              .collect(Collectors.toSet());
      log.info(
          "Checked {} proxies, {} available",
          proxies0.size(),
          checked.stream().filter(x -> x.getResponseTime() != -1).count());

      var exists =
          proxyRepository
              .findByIpPortIn(checked.stream().map(Proxy::getIpPort).collect(Collectors.toSet()))
              .stream()
              .map(Proxy::getIpPort)
              .collect(Collectors.toSet());
      var notExists =
          checked.stream().filter(x -> !exists.contains(x.getIpPort())).collect(Collectors.toSet());
      log.info("Save {} proxies", notExists.size());
      proxyRepository.saveAll(notExists);
    }
  }
}
