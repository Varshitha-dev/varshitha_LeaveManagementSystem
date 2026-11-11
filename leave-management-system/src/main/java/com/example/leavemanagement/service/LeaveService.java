package com.example.leavemanagement.service;

import com.example.leavemanagement.model.*;
import com.example.leavemanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveService {
    private final LeaveRequestRepository requests;
    private final LeaveBalanceRepository balances;
    private final EmployeeRepository employees;

    public LeaveService(LeaveRequestRepository requests, LeaveBalanceRepository balances, EmployeeRepository employees) {
        this.requests = requests; this.balances = balances; this.employees = employees;
    }

    public List<LeaveRequest> history(Long employeeId) {
        return requests.findByEmployeeIdOrderByAppliedAtDesc(employeeId);
    }

    public List<LeaveBalance> balances(Long employeeId) {
        return balances.findByEmployeeId(employeeId);
    }

   public List<LeaveRequest> pendingRequests() {
    return requests.findByStatusWithEmployee(LeaveStatus.PENDING);
}

public List<LeaveRequest> allRequests() {
    return requests.findAllWithEmployee();
}

    @Transactional
    public void apply(String username, LeaveType type, LocalDate start, LocalDate end, String reason) {
        Employee employee = employees.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Employee account was not found."));
        if (start == null || end == null) throw new IllegalArgumentException("Please select both start and end dates.");
        if (start.isBefore(LocalDate.now())) throw new IllegalArgumentException("Start date cannot be in the past.");
        if (end.isBefore(start)) throw new IllegalArgumentException("End date must be on or after the start date.");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("Please enter a reason for leave.");
        if (reason.length() > 500) throw new IllegalArgumentException("Reason must be 500 characters or fewer.");

        int days = Math.toIntExact(ChronoUnit.DAYS.between(start, end) + 1);
        var balance = balances.findByEmployeeIdAndLeaveType(employee.getId(), type)
            .orElseThrow(() -> new IllegalArgumentException("No leave balance is configured for " + type + "."));
        if (balance.getRemainingDays() < days) {
            throw new IllegalArgumentException("Insufficient " + type.name().toLowerCase() +
                " leave balance. Available: " + balance.getRemainingDays() + " day(s); requested: " + days + ".");
        }
        boolean overlap = requests.existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            employee.getId(), List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED), end, start);
        if (overlap) throw new IllegalArgumentException("These dates overlap an existing pending or approved leave request.");
        requests.save(new LeaveRequest(employee, type, start, end, days, reason.trim()));
    }

    @Transactional
    public void approve(Long requestId, String comment) {
        LeaveRequest request = requests.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Leave request not found."));
        if (request.getStatus() != LeaveStatus.PENDING) throw new IllegalArgumentException("This request has already been processed.");
        var balance = balances.findByEmployeeIdAndLeaveType(request.getEmployee().getId(), request.getLeaveType())
            .orElseThrow(() -> new IllegalArgumentException("Leave balance not found."));
        if (balance.getRemainingDays() < request.getDays()) {
            throw new IllegalArgumentException("Cannot approve: employee no longer has enough leave balance.");
        }
        balance.useDays(request.getDays());
        request.approve(comment == null ? "" : comment.trim());
    }

    @Transactional
    public void reject(Long requestId, String comment) {
        LeaveRequest request = requests.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Leave request not found."));
        if (request.getStatus() != LeaveStatus.PENDING) throw new IllegalArgumentException("This request has already been processed.");
        request.reject(comment == null ? "" : comment.trim());
    }
}
