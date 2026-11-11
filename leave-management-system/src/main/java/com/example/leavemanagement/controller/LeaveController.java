package com.example.leavemanagement.controller;

import com.example.leavemanagement.model.*;
import com.example.leavemanagement.repository.EmployeeRepository;
import com.example.leavemanagement.service.LeaveService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
public class LeaveController {
    private final LeaveService leaveService;
    private final EmployeeRepository employees;

    public LeaveController(LeaveService leaveService, EmployeeRepository employees) {
        this.leaveService = leaveService; this.employees = employees;
    }

    @GetMapping("/")
    public String home(Authentication auth) {
        var employee = employees.findByUsername(auth.getName()).orElseThrow();
        return employee.getRole() == Role.MANAGER ? "redirect:/manager/dashboard" : "redirect:/employee/dashboard";
    }

    @GetMapping("/employee/dashboard")
    public String dashboard(Authentication auth, Model model) {
        var employee = employees.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("employee", employee);
        model.addAttribute("balances", leaveService.balances(employee.getId()));
        model.addAttribute("requests", leaveService.history(employee.getId()));
        return "employee-dashboard";
    }

    @GetMapping("/employee/apply")
    public String applyPage(Authentication auth, Model model) {
        var employee = employees.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("employee", employee);
        model.addAttribute("balances", leaveService.balances(employee.getId()));
        model.addAttribute("leaveTypes", LeaveType.values());
        model.addAttribute("today", LocalDate.now());
        return "apply-leave";
    }

    @PostMapping("/employee/apply")
    public String apply(Authentication auth,
                        @RequestParam LeaveType leaveType,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                        @RequestParam @NotBlank String reason,
                        RedirectAttributes redirect) {
        try {
            leaveService.apply(auth.getName(), leaveType, startDate, endDate, reason);
            redirect.addFlashAttribute("success", "Your leave request has been submitted for approval.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/employee/apply";
        }
        return "redirect:/employee/dashboard";
    }

    @GetMapping("/employee/history")
    public String history(Authentication auth, Model model) {
        var employee = employees.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("employee", employee);
        model.addAttribute("requests", leaveService.history(employee.getId()));
        return "leave-history";
    }

    @GetMapping("/manager/dashboard")
    public String managerDashboard(Authentication auth, Model model) {
        var manager = employees.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("manager", manager);
        model.addAttribute("pending", leaveService.pendingRequests());
        model.addAttribute("allRequests", leaveService.allRequests());
        return "manager-dashboard";
    }

    @PostMapping("/manager/approve/{id}")
    public String approve(@PathVariable Long id, @RequestParam(required = false) String comment,
                          RedirectAttributes redirect) {
        try {
            leaveService.approve(id, comment);
            redirect.addFlashAttribute("success", "Leave request approved.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/manager/dashboard";
    }

    @PostMapping("/manager/reject/{id}")
    public String reject(@PathVariable Long id, @RequestParam(required = false) String comment,
                         RedirectAttributes redirect) {
        try {
            leaveService.reject(id, comment);
            redirect.addFlashAttribute("success", "Leave request rejected.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/manager/dashboard";
    }
}
