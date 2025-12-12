package uz.billsplitter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.billsplitter.dto.items.ItemBreakdownDto;
import uz.billsplitter.dto.items.ItemDto;
import uz.billsplitter.dto.participant.ParticipantCalculationDto;
import uz.billsplitter.dto.participant.ParticipantRequestDto;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BillMapper {

    @Mapping(target = "itemName", source = "item.name")
    @Mapping(target = "itemPrice", source = "item.price")
    @Mapping(target = "sharedWith", source = "shareCount")
    ItemBreakdownDto toItemBreakdown(ItemDto item, Integer shareCount, BigDecimal individualShare);

    @Mapping(target = "participantId", source = "participant.id")
    @Mapping(target = "participantName", source = "participant.name")
    ParticipantCalculationDto toParticipantCalculation(
        ParticipantRequestDto participant,
        BigDecimal subtotal,
        BigDecimal serviceCharge,
        BigDecimal totalToPay,
        List<ItemBreakdownDto> itemBreakdowns
    );
}
