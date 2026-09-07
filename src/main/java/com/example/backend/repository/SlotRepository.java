package com.example.backend.repository;

import com.example.backend.entity.Slot;
import com.example.backend.enums.SlotStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

import com.example.backend.entity.Subject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    List<Slot> findByStatusAndStartTimeAfter(SlotStatus status, LocalDateTime after);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    Optional<Slot> findWithLockById(Long id);

    List<Slot> findBySubjectIdAndStatusAndStartTimeBetween(
        Long subjectId, SlotStatus status, LocalDateTime from, LocalDateTime to
    );

    @Query("""  
        SELECT COUNT(s) > 0 FROM Slot s
        WHERE s.subject = :subject
            AND s.status <> com.example.backend.enums.SlotStatus.CANCELLED
            AND s.id <> COALESCE(:excludeId, -1)
            AND s.startTime < :endTime
            AND s.endTime > :startTime"""   
        )
    boolean existsOverlapping(@Param("subject") Subject subject,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime,
                              @Param("excludeId") Long excludeId);

    @Query("SELECT s FROM Slot s JOIN FETCH s.subject WHERE s.id = :id")
    Optional<Slot> findByIdWithSubject(@Param("id") Long id);
}

