package com.example.leavemanagement.config;

import com.example.leavemanagement.model.*;
import com.example.leavemanagement.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedData(EmployeeRepository employees, LeaveBalanceRepository balances,
                               PasswordEncoder encoder) {
        return args -> {
            if (employees.existsByUsername("manager")) return;

            Employee manager = employees.save(new Employee("manager", encoder.encode("Manager@123"),
                "Davis Levin", "manager@example.com", "Human Resources", Role.MANAGER));
            Employee employee = employees.save(new Employee("employee", encoder.encode("Employee@123"),
                "Lina Ahmad", "employee@example.com", "Engineering", Role.EMPLOYEE));
            Employee employee2 = employees.save(new Employee("jacob", encoder.encode("Employee@123"),
                "Jacob Yuen", "jacob@example.com", "Operations", Role.EMPLOYEE));

            for (Employee e : new Employee[]{employee, employee2}) {
                balances.save(new LeaveBalance(e, LeaveType.ANNUAL, 12));
                balances.save(new LeaveBalance(e, LeaveType.SICK, 6));
                balances.save(new LeaveBalance(e, LeaveType.CASUAL, 3));
            }
        };
    }
}
