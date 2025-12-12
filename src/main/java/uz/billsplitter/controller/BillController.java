package uz.billsplitter.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.billsplitter.dto.bills.BillSplitRequestDto;
import uz.billsplitter.dto.bills.BillSplitResponseDto;
import uz.billsplitter.service.BillService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bills")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BillController {

    BillService billService;

    @PostMapping("/splitting")
    public ResponseEntity<BillSplitResponseDto> split(@Valid @RequestBody BillSplitRequestDto request) {
        BillSplitResponseDto response = billService.split(request);
        return ResponseEntity.ok(response);
    }
}
