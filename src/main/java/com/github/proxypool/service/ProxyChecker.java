package com.github.proxypool.service;

import com.github.proxypool.dto.ProxyDto;
import com.github.proxypool.utils.GEOUtil;
import com.maxmind.geoip2.model.AbstractCityResponse;
import com.maxmind.geoip2.model.AbstractCountryResponse;
import com.maxmind.geoip2.record.AbstractNamedRecord;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy.Type;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class ProxyChecker {
  private static final String TEST_URL_HTTP_HEADERS = "http://httpbin.org/headers";

  private static final int CONNECT_TIMEOUT = 10; // 连接超时时间，单位：秒
  private static final int READ_TIMEOUT = 10; // 读取超时时间，单位：秒
  private static final int WRITE_TIMEOUT = 10; // 写入超时时间，单位：秒

  private static final OkHttpClient httpClient = new OkHttpClient.Builder()
          .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
          .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
          .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
          .build();

  public Set<ProxyDto> check(Set<String> proxies) {
    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    List<CompletableFuture<ProxyDto>> futureList =
            proxies.parallelStream()
                   .map(proxy -> CompletableFuture.supplyAsync(() -> checkAndReturnProxy(proxy), executor))
                   .toList();

    CompletableFuture<Void> allOf =
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));

    CompletableFuture<Set<ProxyDto>> allResults =
            allOf.thenApply(
                    v ->
                            futureList.stream()
                                      .map(CompletableFuture::join)
                                      .filter(Objects::nonNull)
                                      .collect(Collectors.toSet()));

    return allResults.join();
  }

  private ProxyDto checkAndReturnProxy(String proxy) {
    try {
      return checkProxy(proxy);
    } catch (Exception e) {
        return null;
    }
  }

  private ProxyDto checkProxy(String proxy) {
    String[] parts = proxy.split(":");
    String host = parts[0];
    int port = Integer.parseInt(parts[1]);

    var okHttpProxy = new java.net.Proxy(Type.HTTP, new InetSocketAddress(host, port));

    OkHttpClient client = httpClient.newBuilder().proxy(okHttpProxy).build();

    Pair<Long, Boolean> result = testProxy(client, TEST_URL_HTTP_HEADERS);

    var rt = result.getFirst();
    var cityResponse = GEOUtil.getCityResponse(host);

    return new ProxyDto()
            .setIpPort(proxy)
            .setAnonymous(result.getSecond())
            .setCountry(
                    cityResponse
                            .map(AbstractCountryResponse::getCountry)
                            .map(AbstractNamedRecord::getName)
                            .orElse("Unknown"))
            .setCity(
                    cityResponse
                            .map(AbstractCityResponse::getCity)
                            .map(AbstractNamedRecord::getName)
                            .orElse("Unknown"))
            .setResponseTime(rt)
            .setLastCheckTime(ZonedDateTime.now());
  }

  private Pair<Long, Boolean> testProxy(OkHttpClient client, String url) {
    Request request = new Request.Builder().url(url).build();

    long startTime = System.currentTimeMillis();
    try (Response response = client.newCall(request).execute()) {
      if (response.code() == 200) {
        long responseTime = System.currentTimeMillis() - startTime;
        String responseBody = Objects.requireNonNull(response.body()).string();
        boolean isAnonymous =
                !responseBody.contains("X-Forwarded-For") && !responseBody.contains("Via");
        return Pair.of(responseTime, isAnonymous);
      }
    } catch (IOException e) {
      // Log the exception if needed
    }
    return Pair.of(-1L, false); // Indicate failure
  }
}
