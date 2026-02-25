package com.goodoc.demo.controller;

import com.goodoc.demo.dto.AppointmentResponse;
import com.goodoc.demo.dto.CancelRequest;
import com.goodoc.demo.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final AppointmentService appointmentService;

    public ReservationController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // 예약 취소 (사유 포함)
    @DeleteMapping("/{id}")
    public ResponseEntity<AppointmentResponse> cancelReservation(
            @PathVariable Long id,
            @Valid @RequestBody CancelRequest request) {
        AppointmentResponse response = appointmentService.cancelAppointment(id, request.getReason());
        return ResponseEntity.ok(response);
    }
}
