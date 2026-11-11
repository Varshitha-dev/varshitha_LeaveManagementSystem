
package com.example.leavemanagement.repository;

import com.example.leavemanagement.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeIdOrderByAppliedAtDesc(Long employeeId);

    // Fetch all requests together with employee details
    @Query("SELECT r FROM LeaveRequest r JOIN FETCH r.employee ORDER BY r.appliedAt DESC")
    List<LeaveRequest> findAllWithEmployee();

    // Fetch requests of a particular status together with employee details
    @Query("SELECT r FROM LeaveRequest r JOIN FETCH r.employee WHERE r.status = :status ORDER BY r.appliedAt ASC")
    List<LeaveRequest> findByStatusWithEmployee(@Param("status") LeaveStatus status);

    Optional<LeaveRequest> findByIdAndEmployeeId(Long id, Long employeeId);

    boolean existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        Long employeeId,
        List<LeaveStatus> statuses,
        LocalDate endDate,
        LocalDate startDate
    );
}

