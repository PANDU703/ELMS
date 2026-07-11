package com.elms.employee_leave_management_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/employees")
    public String employees() {
        return "employees";
    }

    @GetMapping("/apply-leave")
    public String applyLeave() {
        return "apply-leave";
    }

    @GetMapping("/leave-history")
    public String leaveHistory() {
        return "leave-history";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
}