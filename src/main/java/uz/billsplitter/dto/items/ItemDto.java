package uz.billsplitter.dto.items;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ItemDto(
    @NotBlank(message = "Name cant be empty or null")
    String name,
    @NotNull(message = "Price cant be null")
    @DecimalMin(value = "0.01")
    BigDecimal price,
    @NotEmpty(message = "Participants cant be null")
    List<Long> participantIds
) {
}
