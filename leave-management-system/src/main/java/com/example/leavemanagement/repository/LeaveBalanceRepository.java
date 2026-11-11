package com.example.leavemanagement.repository;

import com.example.leavemanagement.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    List<LeaveBalance> findByEmployeeId(Long employeeId);
    Optional<LeaveBalance> findByEmployeeIdAndLeaveType(Long employeeId, LeaveType leaveType);
}
