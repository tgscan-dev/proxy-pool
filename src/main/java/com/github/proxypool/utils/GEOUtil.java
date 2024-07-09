package com.github.proxypool.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

public class GEOUtil {

  private static File database = null;

  static {
    var path = GEOUtil.class.getClassLoader().getResource("GeoLite2-City.mmdb").getPath();
    database = new File(path);
  }

  public static Optional<CityResponse> getCityResponse(String ip) {
    try {
      DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
      InetAddress ipAddress = InetAddress.getByName(ip);
      return Optional.of(dbReader.city(ipAddress));
    } catch (IOException | GeoIp2Exception e) {
      return Optional.empty();
    }
  }
}
