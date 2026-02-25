package com.goodoc.demo.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AppointmentRescheduleRequest {

    @NotNull(message = "변경할 예약 날짜는 필수입니다.")
    @Future(message = "예약 날짜는 현재 시간 이후여야 합니다.")
    private LocalDateTime appointmentDate;

    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }
}
