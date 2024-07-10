package com.github.proxypool.dto;

import com.github.proxypool.domain.Proxy;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProxyDto {
  private String ipPort;
  private boolean isAnonymous;
  private String country;
  private String city;
  private long responseTime;
  private ZonedDateTime lastCheckTime;

  public static Proxy toProxy(ProxyDto proxyDto) {
    Proxy proxy = new Proxy();
    proxy.setIpPort(proxyDto.getIpPort());
    proxy.setAnonymous(proxyDto.isAnonymous());
    proxy.setCountry(proxyDto.getCountry());
    proxy.setCity(proxyDto.getCity());
    proxy.setResponseTime(proxyDto.getResponseTime());
    proxy.setLastCheckTime(proxyDto.getLastCheckTime());
    proxy.setFailCount(proxyDto.getResponseTime() == -1 ? 1 : 0);
    proxy.setFirstCheckTime(ZonedDateTime.now());
    return proxy;
  }
}
