package com.goodoc.demo.repository;

import com.goodoc.demo.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientName(String patientName);
    List<Appointment> findByDoctorName(String doctorName);
}
