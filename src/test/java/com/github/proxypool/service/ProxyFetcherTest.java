package com.github.proxypool.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProxyFetcherTest {

	@Autowired
	private ProxyFetcher proxyFetcher;

	@Test
	void getHttpProxies() {
		var proxies = proxyFetcher.getHttpProxies();
		System.out.println(proxies.size());
	}
}