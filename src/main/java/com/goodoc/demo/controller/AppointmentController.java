package com.goodoc.demo.controller;

import com.goodoc.demo.dto.AppointmentRequest;
import com.goodoc.demo.dto.AppointmentResponse;
import com.goodoc.demo.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // 예약 생성
    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 단건 예약 조회
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointment(@PathVariable Long id) {
        AppointmentResponse response = appointmentService.getAppointment(id);
        return ResponseEntity.ok(response);
    }

    // 전체 예약 조회 (환자 이름 필터 선택)
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments(
            @RequestParam(required = false) String patientName) {
        List<AppointmentResponse> responses = appointmentService.getAllAppointments(patientName);
        return ResponseEntity.ok(responses);
    }

    // 예약 취소
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable Long id) {
        AppointmentResponse response = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(response);
    }
}
