package com.github.proxypool.controller;

import com.github.proxypool.domain.Proxy;
import com.github.proxypool.repository.ProxyRepository;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

  @Autowired
  private ProxyRepository proxyRepository;

  @GetMapping("/")
  public String home(
          @RequestParam(value = "isAnonymous", required = false) Boolean isAnonymous,
          @RequestParam(value = "country", required = false) String country,
          @RequestParam(value = "city", required = false) String city,
          @RequestParam(value = "page", required = false, defaultValue = "1") int page,
          @RequestParam(value = "sortBy", required = false, defaultValue = "lastCheckTime") String sortBy,
          Model model) {

    if (country != null && country.trim().isEmpty()) {
      country = null;
    }
    if (city != null && city.trim().isEmpty()) {
      city = null;
    }

    Sort sort = sortBy.equals("responseTime")
            ? Sort.by(Sort.Order.asc("responseTime"))
            : Sort.by(Sort.Order.desc("lastCheckTime"));

    Page<Proxy> proxies = proxyRepository.findProxies(isAnonymous, country, city, PageRequest.of(page - 1, 20, sort));

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    List<ProxyDto> proxyDtos = proxies.getContent().stream()
                                      .map(proxy -> new ProxyDto(
                                              proxy.getIpPort(),
                                              proxy.isAnonymous() ? "Yes" : "No",
                                              proxy.getCountry(),
                                              proxy.getCity(),
                                              proxy.getResponseTime() + "ms",
                                              proxy.getLastCheckTime().toEpochSecond()

                                      ))
                                      .collect(Collectors.toList());

    model.addAttribute("proxies", proxyDtos);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", proxies.getTotalPages());
    model.addAttribute("isAnonymous", isAnonymous);
    model.addAttribute("country", country);
    model.addAttribute("city", city);
    model.addAttribute("sortBy", sortBy);
    return "index";
  }

  @Data
  @Accessors(chain = true)
  public static class ProxyDto {
    private String ipPort;
    private String isAnonymous;
    private String country;
    private String city;
    private String responseTime;
    private Long lastCheckTime;

    public ProxyDto(String ipPort, String isAnonymous, String country, String city, String responseTime, Long lastCheckTime) {
      this.ipPort = ipPort;
      this.isAnonymous = isAnonymous;
      this.country = country;
      this.city = city;
      this.responseTime = responseTime;
      this.lastCheckTime = lastCheckTime;
    }
  }
}
