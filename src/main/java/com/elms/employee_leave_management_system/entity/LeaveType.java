package com.elms.employee_leave_management_system.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_types")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "leave_name", nullable = false, unique = true)
    private String leaveName;

    private String description;

    @Column(name = "max_days")
    private Integer maxDays;

    public LeaveType() {
    }

    public LeaveType(String leaveName, String description, Integer maxDays) {
        this.leaveName = leaveName;
        this.description = description;
        this.maxDays = maxDays;
    }

    public Long getId() {
        return id;
    }

    public String getLeaveName() {
        return leaveName;
    }

    public void setLeaveName(String leaveName) {
        this.leaveName = leaveName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxDays() {
        return maxDays;
    }

    public void setMaxDays(Integer maxDays) {
        this.maxDays = maxDays;
    }
}