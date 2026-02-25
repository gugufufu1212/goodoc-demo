package com.goodoc.demo.dto;

import com.goodoc.demo.entity.Appointment;
import java.time.LocalDateTime;

public class AppointmentResponse {

    private Long id;
    private String patientName;
    private String doctorName;
    private String department;
    private LocalDateTime appointmentDate;
    private String status;
    private LocalDateTime createdAt;
    private String cancelReason;

    public static AppointmentResponse from(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.id = appointment.getId();
        response.patientName = appointment.getPatientName();
        response.doctorName = appointment.getDoctorName();
        response.department = appointment.getDepartment();
        response.appointmentDate = appointment.getAppointmentDate();
        response.status = appointment.getStatus().name();
        response.createdAt = appointment.getCreatedAt();
        response.cancelReason = appointment.getCancelReason();
        return response;
    }

    // Getters
    public Long getId() { return id; }
    public String getPatientName() { return patientName; }
    public String getDoctorName() { return doctorName; }
    public String getDepartment() { return department; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getCancelReason() { return cancelReason; }
}
