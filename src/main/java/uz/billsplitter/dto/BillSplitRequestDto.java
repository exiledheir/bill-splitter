package uz.billsplitter.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record BillSplitRequestDto(
    @NotEmpty(message = "Participants list cant be empty")
    @Valid
    List<ParticipantRequestDto> participants,
    @NotEmpty(message = "Items list cant be empty")
    @Valid
    List<ItemDto> items,
    @NotNull(message = "Comission is mandatory")
    @DecimalMin(value = "0.0")
    BigDecimal serviceChargePercent
) {
}
