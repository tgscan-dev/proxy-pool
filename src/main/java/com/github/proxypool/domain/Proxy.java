package com.github.proxypool.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "proxy", uniqueConstraints = {@UniqueConstraint(columnNames = {"ipPort"})})
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Accessors(chain = true)
public class Proxy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String ipPort;
	private boolean isAnonymous;
	private String country;
	private String city;
	private long responseTime;
	private LocalDateTime lastCheckTime;
	private LocalDateTime firstCheckTime;
	private long failCount;


	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Proxy proxy = (Proxy) o;
		return getId() != null && Objects.equals(getId(), proxy.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}
