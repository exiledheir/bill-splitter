package uz.billsplitter.dto.participant;

import uz.billsplitter.dto.items.ItemBreakdownDto;

import java.math.BigDecimal;
import java.util.List;

public record ParticipantCalculationDto(
    Long participantId,
    String participantName,
    BigDecimal subtotal,
    BigDecimal serviceCharge,
    BigDecimal totalToPay,
    List<ItemBreakdownDto> itemBreakdowns
) {
}
