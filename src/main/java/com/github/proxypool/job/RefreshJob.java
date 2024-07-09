package com.github.proxypool.job;

import com.github.proxypool.domain.Proxy;
import com.github.proxypool.repository.ProxyRepository;
import com.github.proxypool.service.ProxyChecker;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RefreshJob {

  @Autowired private ProxyChecker proxyChecker;
  @Autowired private ProxyRepository proxyRepository;

  @Scheduled(fixedRate = 1000 * 60 * 3)
  public void refresh() {

    log.info("Refresh job started");
    var proxies =
        proxyRepository.findByLastCheckTimeBeforeAndFailCountLessThanEqual(
            LocalDateTime.now().minusMinutes(5), 5);
    var hostPorts2proxy =
        proxies.stream().collect(Collectors.toMap(Proxy::getIpPort, proxy -> proxy));
    var hostPorts = proxies.stream().map(Proxy::getIpPort).collect(Collectors.toSet());
    var availableProxies = proxyChecker.check(hostPorts);
    availableProxies.forEach(
        proxy -> {
          var dbProxy = hostPorts2proxy.get(proxy.getIpPort());
          var fail = proxy.getResponseTime() == -1 ? dbProxy.getFailCount() + 1 : 0;
          dbProxy.setLastCheckTime(LocalDateTime.now());
          dbProxy.setResponseTime(proxy.getResponseTime());
          dbProxy.setFailCount(fail);
        });
    proxyRepository.saveAll(proxies);
  }
}
