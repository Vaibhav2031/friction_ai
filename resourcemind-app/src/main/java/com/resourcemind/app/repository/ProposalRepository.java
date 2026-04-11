package com.resourcemind.app.repository;

import com.resourcemind.app.domain.Proposal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface ProposalRepository extends JpaRepository<Proposal, UUID> {

	Optional<Proposal> findByPublicId(String publicId);

	List<Proposal> findAllByOrderByCreatedAtDesc();

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Proposal p where p.id = :id")
	Optional<Proposal> findByIdForUpdate(@Param("id") UUID id);
}
