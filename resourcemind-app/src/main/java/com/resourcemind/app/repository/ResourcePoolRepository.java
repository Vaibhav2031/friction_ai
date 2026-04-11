package com.resourcemind.app.repository;

import com.resourcemind.app.domain.ResourcePool;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface ResourcePoolRepository extends JpaRepository<ResourcePool, Long> {

	Optional<ResourcePool> findByPoolName(String poolName);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select r from ResourcePool r where r.poolName = :poolName")
	Optional<ResourcePool> findByPoolNameForUpdate(@Param("poolName") String poolName);
}
