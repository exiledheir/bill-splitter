package uz.billsplitter.dto;

import java.math.BigDecimal;
import java.util.List;

public record BillSplitResponseDto(
    BigDecimal totalAmount,
    BigDecimal serviceCharge,
    BigDecimal grandTotal,
    List<ParticipantCalculationDto> participantCalculations
) {
}
