package com.example.leavemanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_balances", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "leave_type"}))
public class LeaveBalance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false, length = 20)
    private LeaveType leaveType;

    @Column(nullable = false)
    private int totalDays;

    @Column(nullable = false)
    private int usedDays;

    protected LeaveBalance() {}

    public LeaveBalance(Employee employee, LeaveType leaveType, int totalDays) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.totalDays = totalDays;
        this.usedDays = 0;
    }

    public int getRemainingDays() { return totalDays - usedDays; }
    public LeaveType getLeaveType() { return leaveType; }
    public int getTotalDays() { return totalDays; }
    public int getUsedDays() { return usedDays; }
    public void useDays(int days) { this.usedDays += days; }
}
