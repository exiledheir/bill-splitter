package uz.billsplitter.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.billsplitter.dto.bills.BillSplitRequestDto;
import uz.billsplitter.dto.bills.BillSplitResponseDto;
import uz.billsplitter.dto.items.ItemBreakdownDto;
import uz.billsplitter.dto.items.ItemDto;
import uz.billsplitter.dto.participant.ParticipantCalculationDto;
import uz.billsplitter.dto.participant.ParticipantRequestDto;
import uz.billsplitter.exception.ParticipantNotFoundException;
import uz.billsplitter.mapper.BillMapper;
import uz.billsplitter.service.BillService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BillServiceImpl implements BillService {

    private static final int DECIMAL_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    BillMapper billMapper;

    @Override
    public BillSplitResponseDto split(BillSplitRequestDto request) {
        validateParticipants(request);

        Map<Long, ParticipantRequestDto> participantMap = createParticipantMap(request.participants());
        Map<Long, BigDecimal> participantSubtotals = calculateParticipantSubtotals(
            request.items(), participantMap);
        Map<Long, List<ItemBreakdownDto>> participantBreakdowns = calculateItemBreakdowns(
            request.items(), participantMap);

        BigDecimal totalAmount = calculateTotalAmount(request.items());
        BigDecimal serviceCharge = calculateServiceCharge(totalAmount, request.serviceChargePercent());
        BigDecimal grandTotal = totalAmount.add(serviceCharge);

        List<ParticipantCalculationDto> participantCalculations = createParticipantCalculations(
            participantMap, participantSubtotals, participantBreakdowns,
            grandTotal, totalAmount, request.serviceChargePercent());

        return new BillSplitResponseDto(
            totalAmount.setScale(DECIMAL_SCALE, ROUNDING_MODE),
            serviceCharge.setScale(DECIMAL_SCALE, ROUNDING_MODE),
            grandTotal.setScale(DECIMAL_SCALE, ROUNDING_MODE),
            participantCalculations
        );
    }

    private void validateParticipants(BillSplitRequestDto request) {
        Set<Long> participantIds = request.participants().stream()
            .map(ParticipantRequestDto::id)
            .collect(Collectors.toSet());

        for (ItemDto item : request.items()) {
            for (Long participantId : item.participantIds()) {
                if (!participantIds.contains(participantId)) {
                    throw new ParticipantNotFoundException(
                        String.format("Participant with ID %d not found in participants list for item '%s'",
                            participantId, item.name()));
                }
            }
        }
    }

    private Map<Long, ParticipantRequestDto> createParticipantMap(List<ParticipantRequestDto> participants) {
        return participants.stream()
            .collect(Collectors.toMap(ParticipantRequestDto::id, p -> p));
    }

    private Map<Long, BigDecimal> calculateParticipantSubtotals(
        List<ItemDto> items, Map<Long, ParticipantRequestDto> participantMap) {

        Map<Long, BigDecimal> subtotals = new HashMap<>();
        participantMap.keySet().forEach(id -> subtotals.put(id, BigDecimal.ZERO));

        for (ItemDto item : items) {
            int shareCount = item.participantIds().size();
            BigDecimal itemShare = item.price()
                .divide(BigDecimal.valueOf(shareCount), DECIMAL_SCALE, ROUNDING_MODE);

            for (Long participantId : item.participantIds()) {
                subtotals.merge(participantId, itemShare, BigDecimal::add);
            }
        }

        return subtotals;
    }

    private Map<Long, List<ItemBreakdownDto>> calculateItemBreakdowns(
        List<ItemDto> items, Map<Long, ParticipantRequestDto> participantMap) {

        Map<Long, List<ItemBreakdownDto>> breakdowns = new HashMap<>();
        participantMap.keySet().forEach(id -> breakdowns.put(id, new ArrayList<>()));

        for (ItemDto item : items) {
            int shareCount = item.participantIds().size();
            BigDecimal individualShare = item.price()
                .divide(BigDecimal.valueOf(shareCount), DECIMAL_SCALE, ROUNDING_MODE);

            ItemBreakdownDto breakdown = billMapper.toItemBreakdown(item, shareCount, individualShare);

            for (Long participantId : item.participantIds()) {
                breakdowns.get(participantId).add(breakdown);
            }
        }

        return breakdowns;
    }

    private BigDecimal calculateTotalAmount(List<ItemDto> items) {
        return items.stream()
            .map(ItemDto::price)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateServiceCharge(BigDecimal totalAmount, BigDecimal serviceChargePercent) {
        return totalAmount
            .multiply(serviceChargePercent)
            .divide(BigDecimal.valueOf(100), DECIMAL_SCALE, ROUNDING_MODE);
    }

    private List<ParticipantCalculationDto> createParticipantCalculations(
        Map<Long, ParticipantRequestDto> participantMap,
        Map<Long, BigDecimal> participantSubtotals,
        Map<Long, List<ItemBreakdownDto>> participantBreakdowns,
        BigDecimal grandTotal,
        BigDecimal totalAmount,
        BigDecimal serviceChargePercent) {

        return participantMap.values().stream()
            .map(participant -> {
                BigDecimal subtotal = participantSubtotals.get(participant.id());

                BigDecimal participantServiceCharge = grandTotal
                    .subtract(totalAmount)
                    .multiply(subtotal)
                    .divide(totalAmount, DECIMAL_SCALE, ROUNDING_MODE);

                BigDecimal totalToPay = subtotal.add(participantServiceCharge);

                return billMapper.toParticipantCalculation(
                    participant,
                    subtotal.setScale(DECIMAL_SCALE, ROUNDING_MODE),
                    participantServiceCharge.setScale(DECIMAL_SCALE, ROUNDING_MODE),
                    totalToPay.setScale(DECIMAL_SCALE, ROUNDING_MODE),
                    participantBreakdowns.get(participant.id())
                );
            })
            .sorted(Comparator.comparing(ParticipantCalculationDto::participantId))
            .collect(Collectors.toList());
    }
}