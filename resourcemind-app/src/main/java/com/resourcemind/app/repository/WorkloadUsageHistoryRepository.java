package com.resourcemind.app.repository;

import com.resourcemind.app.domain.WorkloadUsageHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkloadUsageHistoryRepository extends JpaRepository<WorkloadUsageHistory, Long> {

	@Query(
			"""
			select h from WorkloadUsageHistory h
			where h.useCaseKey = :useCaseKey
			and (:profile is null or h.workloadProfile = :profile)
			order by h.periodEnd desc
			"""
	)
	List<WorkloadUsageHistory> findRecent(@Param("useCaseKey") String useCaseKey, @Param("profile") String profile);
}
