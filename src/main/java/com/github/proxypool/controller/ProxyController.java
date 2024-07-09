package com.github.proxypool.controller;

import com.github.proxypool.domain.Proxy;
import com.github.proxypool.repository.ProxyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProxyController {

  @Autowired private ProxyRepository proxyRepository;

  @GetMapping("/proxy")
  public Page<Proxy> getProxy(
      @RequestParam(value = "isAnonymous", required = false) Boolean isAnonymous,
      @RequestParam(value = "country", required = false) String country,
      @RequestParam(value = "city", required = false) String city,
      @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
    return proxyRepository.findProxies(isAnonymous, country, city, PageRequest.of(page - 1, 20));
  }
}