package uz.billsplitter.dto.items;

import java.math.BigDecimal;

public record ItemBreakdownDto(
    String itemName,
    BigDecimal itemPrice,
    Integer sharedWith,
    BigDecimal individualShare
) {
}