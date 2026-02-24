package com.goodoc.demo.service;

import com.goodoc.demo.dto.AppointmentRequest;
import com.goodoc.demo.dto.AppointmentResponse;
import com.goodoc.demo.entity.Appointment;
import com.goodoc.demo.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Appointment appointment = new Appointment();
        appointment.setPatientName(request.getPatientName());
        appointment.setDoctorName(request.getDoctorName());
        appointment.setDepartment(request.getDepartment());
        appointment.setAppointmentDate(request.getAppointmentDate());

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponse.from(saved);
    }

    public AppointmentResponse getAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다. id=" + id));
        return AppointmentResponse.from(appointment);
    }

    public List<AppointmentResponse> getAllAppointments(String patientName) {
        List<Appointment> appointments = (patientName != null && !patientName.isBlank())
                ? appointmentRepository.findByPatientName(patientName)
                : appointmentRepository.findAll();
        return appointments.stream()
                .map(AppointmentResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다. id=" + id));

        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예약입니다. id=" + id);
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        return AppointmentResponse.from(appointment);
    }
}
