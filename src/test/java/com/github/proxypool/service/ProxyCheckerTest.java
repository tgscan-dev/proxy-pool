package com.github.proxypool.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Collectors;

@SpringBootTest
class ProxyCheckerTest {

	@Autowired
	private ProxyFetcher proxyFetcher;
	@Autowired
	private ProxyChecker proxyChecker;

	@Test
	void check() {
		var httpProxies = proxyFetcher.getHttpProxies();
		var collect = httpProxies.stream().limit(500).collect(Collectors.toSet());
		var check = proxyChecker.check(collect);
		System.out.println(check);

	}
}