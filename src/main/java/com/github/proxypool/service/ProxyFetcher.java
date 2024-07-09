package com.github.proxypool.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProxyFetcher {

	private static final OkHttpClient client = new OkHttpClient();
	private static final Pattern PROXY_PATTERN = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+)");

	@Value("${proxy.http.seed}")
	private String httpSeed;


	public Set<String> fetchProxies(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Unexpected code " + response);
			}
			String responseBody = response.body().string();
			return parseResponse(responseBody);
		}
	}

	private Set<String> parseResponse(String responseBody) {
		Matcher matcher = PROXY_PATTERN.matcher(responseBody);
		Set<String> proxies = new HashSet<>();
		while (matcher.find()) {
			proxies.add(matcher.group(1));
		}
		return proxies;
	}

	public Set<String> getHttpProxies() {

		var urls = Arrays.stream(httpSeed.split(",")).map(String::trim).toList();

		return urls.parallelStream()
		           .map(url -> {
			           try {
				           return fetchProxies(url);
			           } catch (IOException e) {
				           return new HashSet<String>();
			           }
		           })
		           .flatMap(Set::stream)
		           .collect(Collectors.toSet());
	}


}
