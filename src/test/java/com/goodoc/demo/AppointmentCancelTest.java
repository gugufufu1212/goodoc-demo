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
class AppointmentCancelTest {

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
    @DisplayName("정상 취소: reason을 포함한 요청 시 CANCELLED 상태와 cancelReason이 응답에 포함")
    void cancelAppointment_withValidReason_shouldReturnCancelledWithReason() throws Exception {
        Long id = createAppointmentAndGetId();

        CancelRequest cancelRequest = new CancelRequest();
        cancelRequest.setReason("개인 사정으로 취소합니다.");

        mockMvc.perform(patch("/api/appointments/{id}/cancel", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.cancelReason").value("개인 사정으로 취소합니다."));
    }

    @Test
    @DisplayName("취소 사유 누락: reason이 null이면 400 응답")
    void cancelAppointment_withNullReason_shouldReturn400() throws Exception {
        Long id = createAppointmentAndGetId();

        mockMvc.perform(patch("/api/appointments/{id}/cancel", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("취소 사유 빈 문자열: reason이 blank이면 400 응답")
    void cancelAppointment_withBlankReason_shouldReturn400() throws Exception {
        Long id = createAppointmentAndGetId();

        CancelRequest cancelRequest = new CancelRequest();
        cancelRequest.setReason("   ");

        mockMvc.perform(patch("/api/appointments/{id}/cancel", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이미 취소된 예약: 다시 취소하면 409 응답")
    void cancelAppointment_alreadyCancelled_shouldReturn409() throws Exception {
        Long id = createAppointmentAndGetId();

        CancelRequest cancelRequest = new CancelRequest();
        cancelRequest.setReason("첫 번째 취소");

        // 첫 번째 취소 - 성공해야 함
        mockMvc.perform(patch("/api/appointments/{id}/cancel", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk());

        // 두 번째 취소 - 409 응답
        cancelRequest.setReason("두 번째 취소 시도");

        mockMvc.perform(patch("/api/appointments/{id}/cancel", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("존재하지 않는 예약: 404 응답")
    void cancelAppointment_notFound_shouldReturn404() throws Exception {
        CancelRequest cancelRequest = new CancelRequest();
        cancelRequest.setReason("취소 사유");

        mockMvc.perform(patch("/api/appointments/{id}/cancel", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isNotFound());
    }
}
