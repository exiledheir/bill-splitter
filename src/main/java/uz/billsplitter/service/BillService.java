package uz.billsplitter.service;

import uz.billsplitter.dto.BillSplitRequestDto;
import uz.billsplitter.dto.BillSplitResponseDto;

public interface BillService {
    BillSplitResponseDto split(BillSplitRequestDto request);
}
