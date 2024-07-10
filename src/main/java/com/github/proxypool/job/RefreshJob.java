package com.github.proxypool.job;

import com.github.proxypool.domain.Proxy;
import com.github.proxypool.repository.ProxyRepository;
import com.github.proxypool.service.ProxyChecker;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RefreshJob {

  @Autowired private ProxyChecker proxyChecker;
  @Autowired private ProxyRepository proxyRepository;

  @EventListener(ApplicationReadyEvent.class)
  public void refresh() {

    new Thread(
            () -> {
              while (true) {
                  try {
                      doRefresh();
                  } catch (Exception e) {
                      log.error("Error in refresh job", e);
	                  try {
		                  TimeUnit.SECONDS.sleep(5);
	                  } catch (InterruptedException _) {
	                  }
                  }
              }
            })
        .start();
  }

  private void doRefresh() throws InterruptedException {
    log.info("Refresh job started");
    var proxies =
        proxyRepository.findByLastCheckTimeBeforeAndFailCountLessThanEqual(
            ZonedDateTime.now().minusMinutes(5),
            5,
            PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "lastCheckTime")));

    var hostPorts2proxy =
        proxies.stream().collect(Collectors.toMap(Proxy::getIpPort, proxy -> proxy));
    var hostPorts = proxies.stream().map(Proxy::getIpPort).collect(Collectors.toSet());
    var checked = proxyChecker.check(hostPorts);
    checked.forEach(
        proxy -> {
          var dbProxy = hostPorts2proxy.get(proxy.getIpPort());
          var fail = proxy.getResponseTime() == -1 ? dbProxy.getFailCount() + 1 : 0;
          dbProxy.setLastCheckTime(ZonedDateTime.now());
          dbProxy.setResponseTime(proxy.getResponseTime());
          dbProxy.setFailCount(fail);
        });
    if(proxies.isEmpty()){
        log.info("No proxies to check");
        TimeUnit.SECONDS.sleep(5);
        return;
    }
    log.info(
        "Checked {} proxies, {} available",
        hostPorts.size(),
        checked.stream().filter(x -> x.getResponseTime() != -1).count());
    proxyRepository.saveAll(proxies);
  }
}
