package com.example.leavemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private int days;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeaveStatus status = LeaveStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();

    @Column(length = 500)
    private String managerComment;

    protected LeaveRequest() {}

    public LeaveRequest(Employee employee, LeaveType leaveType, LocalDate startDate,
                        LocalDate endDate, int days, String reason) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.days = days;
        this.reason = reason;
        this.status = LeaveStatus.PENDING;
        this.appliedAt = LocalDateTime.now();
    }

    public void approve(String comment) { this.status = LeaveStatus.APPROVED; this.managerComment = comment; }
    public void reject(String comment) { this.status = LeaveStatus.REJECTED; this.managerComment = comment; }
    public Long getId() { return id; }
    public Employee getEmployee() { return employee; }
    public LeaveType getLeaveType() { return leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getDays() { return days; }
    public String getReason() { return reason; }
    public LeaveStatus getStatus() { return status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public String getManagerComment() { return managerComment; }
}
