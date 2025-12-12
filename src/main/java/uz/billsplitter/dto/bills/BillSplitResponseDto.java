package uz.billsplitter.dto.bills;

import uz.billsplitter.dto.participant.ParticipantCalculationDto;

import java.math.BigDecimal;
import java.util.List;

public record BillSplitResponseDto(
    BigDecimal totalAmount,
    BigDecimal serviceCharge,
    BigDecimal grandTotal,
    List<ParticipantCalculationDto> participantCalculations
) {
}
