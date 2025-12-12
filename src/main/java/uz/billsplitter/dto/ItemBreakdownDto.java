package uz.billsplitter.dto;

import java.math.BigDecimal;

public record ItemBreakdownDto(
    String itemName,
    BigDecimal itemPrice,
    Integer sharedWith,
    BigDecimal individualShare
) {
}