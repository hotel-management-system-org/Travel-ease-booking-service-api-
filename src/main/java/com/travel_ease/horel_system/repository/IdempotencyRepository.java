package com.travel_ease.horel_system.repository;


import com.travel_ease.horel_system.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord,Long> {

    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);

}
