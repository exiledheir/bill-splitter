package uz.billsplitter.dto;

import lombok.Builder;
import uz.billsplitter.constant.enums.ErrorType;

import java.util.List;

@Builder
public record ErrorDto(
    int code,
    String message,
    ErrorType type,
    List<String> validationErrors) {}