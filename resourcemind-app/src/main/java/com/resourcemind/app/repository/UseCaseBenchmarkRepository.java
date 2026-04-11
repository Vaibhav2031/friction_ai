package com.resourcemind.app.repository;

import com.resourcemind.app.domain.UseCaseBenchmark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UseCaseBenchmarkRepository extends JpaRepository<UseCaseBenchmark, Long> {

	Optional<UseCaseBenchmark> findByUseCaseTypeAndWorkloadProfile(String useCaseType, String workloadProfile);
}
