package com.goodoc.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodoc.demo.dto.AppointmentRequest;
import com.goodoc.demo.dto.CancelRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppointmentConfirmTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createAppointmentAndGetId() throws Exception {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientName("홍길동");
        request.setDoctorName("김의사");
        request.setDepartment("내과");
        request.setAppointmentDate(LocalDateTime.of(2026, 3, 15, 10, 0));

        MvcResult result = mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    @DisplayName("PENDING 예약 확정 성공: 200 응답, status=CONFIRMED")
    void confirmAppointment_pending_shouldReturnConfirmed() throws Exception {
        Long id = createAppointmentAndGetId();

        mockMvc.perform(patch("/api/appointments/{id}/confirm", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("이미 CONFIRMED 예약 확정 시도: 409 응답")
    void confirmAppointment_alreadyConfirmed_shouldReturn409() throws Exception {
        Long id = createAppointmentAndGetId();

        // 첫 번째 확정 - 성공해야 함
        mockMvc.perform(patch("/api/appointments/{id}/confirm", id))
                .andExpect(status().isOk());

        // 두 번째 확정 - 409 응답
        mockMvc.perform(patch("/api/appointments/{id}/confirm", id))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("CANCELLED 예약 확정 시도: 409 응답")
    void confirmAppointment_cancelled_shouldReturn409() throws Exception {
        Long id = createAppointmentAndGetId();

        // 먼저 취소
        CancelRequest cancelRequest = new CancelRequest();
        cancelRequest.setReason("취소 사유");

        mockMvc.perform(patch("/api/appointments/{id}/cancel", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk());

        // 취소된 예약 확정 시도 - 409 응답
        mockMvc.perform(patch("/api/appointments/{id}/confirm", id))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("존재하지 않는 예약 확정: 404 응답")
    void confirmAppointment_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(patch("/api/appointments/{id}/confirm", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("확정 후 응답에 id, patientName, status 포함 확인")
    void confirmAppointment_responseContainsRequiredFields() throws Exception {
        Long id = createAppointmentAndGetId();

        mockMvc.perform(patch("/api/appointments/{id}/confirm", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.patientName").value("홍길동"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
}
