package com.example.leavemanagement.service;

import com.example.leavemanagement.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final EmployeeRepository employees;
    public CustomUserDetailsService(EmployeeRepository employees) { this.employees = employees; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var employee = employees.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return User.withUsername(employee.getUsername())
            .password(employee.getPassword())
            .roles(employee.getRole().name())
            .build();
    }
}
