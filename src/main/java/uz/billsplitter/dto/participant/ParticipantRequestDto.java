package uz.billsplitter.dto.participant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParticipantRequestDto(
    @NotNull(message = "Enter valid id")
    Long id,
    @NotBlank(message = "Enter valid name")
    String name
) {
}
