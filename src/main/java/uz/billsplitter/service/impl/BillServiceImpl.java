package uz.billsplitter.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.billsplitter.dto.OrderRequestDto;
import uz.billsplitter.dto.OrderResponseDto;
import uz.billsplitter.service.BillService;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BillServiceImpl implements BillService {

    @Override
    public OrderResponseDto split(OrderRequestDto request) {
        return null;
    }
}
