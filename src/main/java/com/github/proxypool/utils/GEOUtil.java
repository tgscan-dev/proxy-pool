package com.github.proxypool.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Optional;

public class GEOUtil {

  private static DatabaseReader dbReader = null;

  static {
    try (InputStream databaseStream = GEOUtil.class.getClassLoader().getResourceAsStream("GeoLite2-City.mmdb")) {
      if (databaseStream != null) {
        dbReader = new DatabaseReader.Builder(databaseStream).build();
      } else {
        throw new IllegalStateException("GeoLite2-City.mmdb not found in resources");
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to initialize DatabaseReader", e);
    }
  }

  public static Optional<CityResponse> getCityResponse(String ip) {
    try {
      InetAddress ipAddress = InetAddress.getByName(ip);
      return Optional.ofNullable(dbReader.city(ipAddress));
    } catch (IOException | GeoIp2Exception e) {
      return Optional.empty();
    }
  }

  public static void main(String[] args) {
    System.out.println(getCityResponse("11.23.2.3"));
  }
}
