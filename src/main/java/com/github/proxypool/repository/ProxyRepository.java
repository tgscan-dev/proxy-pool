package com.github.proxypool.repository;

import com.github.proxypool.domain.Proxy;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProxyRepository extends JpaRepository<Proxy, Long> {

  Page<Proxy> findByLastCheckTimeBeforeAndFailCountLessThanEqual(
          ZonedDateTime lastCheckTime, long failCount,Pageable pageable);

  @Transactional
  long deleteByFailCountGreaterThan(long failCount);

  Set<Proxy> findByIpPortIn(Collection<String> ipPorts);

  @Query(
      "SELECT p FROM Proxy p WHERE "
          + "(:isAnonymous IS NULL OR p.isAnonymous = :isAnonymous) AND "
          + "(:country IS NULL OR p.country = :country) AND "
          + "(:city IS NULL OR p.city = :city) AND "
          + "p.failCount = 0")
  Page<Proxy> findProxies(
      @Param("isAnonymous") Boolean isAnonymous,
      @Param("country") String country,
      @Param("city") String city,
      Pageable pageable);
}
