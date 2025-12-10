package uz.billsplitter.service;

import uz.billsplitter.dto.OrderRequestDto;
import uz.billsplitter.dto.OrderResponseDto;

public interface BillService {
    OrderResponseDto split(OrderRequestDto request);
}
