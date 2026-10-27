package com.aquaguard.dto;

import jakarta.validation.constraints.*;

public record TankRequest(
    @NotBlank @Size(max=100) String name,
    @DecimalMin(value="0.01") double capacityL,
    @DecimalMin(value="0.0") double currentVolumeL,
    @DecimalMin(value="0.0") double incomingFlowLpm,
    @DecimalMin(value="0.0") double outgoingFlowLpm,
    @DecimalMin(value="1.0") @DecimalMax(value="100.0") double warningThresholdPercent
) {}
