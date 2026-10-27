package com.aquaguard.dto;

import jakarta.validation.constraints.*;

public record ReadingRequest(
    @NotBlank @Size(max=80) String zone,
    @DecimalMin("0.0") @DecimalMax("100000.0") double flowRateLpm,
    @DecimalMin("0.0") @DecimalMax("100.0") double tankLevelPercent,
    @NotBlank @Pattern(regexp="SIMULATED|SENSOR") String source
) {}
