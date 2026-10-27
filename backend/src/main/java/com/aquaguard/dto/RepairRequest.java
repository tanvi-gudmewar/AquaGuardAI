package com.aquaguard.dto;

import jakarta.validation.constraints.DecimalMin;

public record RepairRequest(@DecimalMin("0.0") double postRepairFlowLpm) {}
