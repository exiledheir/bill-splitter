package uz.billsplitter.service;

import uz.billsplitter.dto.bills.BillSplitRequestDto;
import uz.billsplitter.dto.bills.BillSplitResponseDto;

public interface BillService {
    BillSplitResponseDto split(BillSplitRequestDto request);
}
