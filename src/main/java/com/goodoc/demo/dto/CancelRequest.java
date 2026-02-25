package com.goodoc.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class CancelRequest {

    @NotBlank(message = "취소 사유는 필수입니다.")
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
