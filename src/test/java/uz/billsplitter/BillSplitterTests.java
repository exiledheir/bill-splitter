package uz.billsplitter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.billsplitter.dto.BillSplitRequestDto;
import uz.billsplitter.dto.ItemBreakdownDto;
import uz.billsplitter.dto.ItemDto;
import uz.billsplitter.dto.ParticipantCalculationDto;
import uz.billsplitter.dto.ParticipantRequestDto;
import uz.billsplitter.mapper.BillMapper;
import uz.billsplitter.service.impl.BillServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BillSplitterTests {

	@Mock
	private BillMapper billMapper;

	@InjectMocks
	private BillServiceImpl billService;

	@BeforeEach
	void setUp() {
		when(billMapper.toItemBreakdown(any(), any(), any())).thenAnswer(invocation -> {
			ItemDto item = invocation.getArgument(0);
			Integer shareCount = invocation.getArgument(1);
			BigDecimal individualShare = invocation.getArgument(2);
			return new ItemBreakdownDto(item.name(), item.price(), shareCount, individualShare);
		});

		when(billMapper.toParticipantCalculation(any(), any(), any(), any(), any())).thenAnswer(invocation -> {
			ParticipantRequestDto participant = invocation.getArgument(0);
			BigDecimal subtotal = invocation.getArgument(1);
			BigDecimal serviceCharge = invocation.getArgument(2);
			BigDecimal totalToPay = invocation.getArgument(3);
			List<ItemBreakdownDto> itemBreakdowns = invocation.getArgument(4);
			return new ParticipantCalculationDto(participant.id(), participant.name(),
				subtotal, serviceCharge, totalToPay, itemBreakdowns);
		});
	}

	@Test
	void testSinglePerson_SingleItem() {
		var participants = List.of(new ParticipantRequestDto(1L, "Mukhammadjon"));
		var items = List.of(new ItemDto("Lavash", new BigDecimal("25000"), List.of(1L)));
		var request = new BillSplitRequestDto(participants, items, new BigDecimal("10.0"));
		var response = billService.split(request);
		var participant = response.participantCalculations().get(0);

		assertEquals("Mukhammadjon", participant.participantName());
		assertEquals(new BigDecimal("25000.00"), participant.subtotal());
		assertEquals(new BigDecimal("2500.00"), participant.serviceCharge());
		assertEquals(new BigDecimal("27500.00"), participant.totalToPay());
	}

	@Test
	void testTwoPeople_SharedItem() {
		var participants = List.of(new ParticipantRequestDto(1L, "Mukhammadjon"), new ParticipantRequestDto(2L, "Sardor"));
		var items = List.of(new ItemDto("Pizza", new BigDecimal("60000"), List.of(1L, 2L)));
		var request = new BillSplitRequestDto(participants, items, new BigDecimal("10.0"));
		var response = billService.split(request);

		var mkh = response.participantCalculations().get(0);
		assertEquals(new BigDecimal("30000.00"), mkh.subtotal());
		assertEquals(new BigDecimal("3000.00"), mkh.serviceCharge());
		assertEquals(new BigDecimal("33000.00"), mkh.totalToPay());

		var sardor = response.participantCalculations().get(1);
		assertEquals(new BigDecimal("30000.00"), sardor.subtotal());
		assertEquals(new BigDecimal("3000.00"), sardor.serviceCharge());
		assertEquals(new BigDecimal("33000.00"), sardor.totalToPay());
	}

	@Test
	void testThreePeople_MixedItems() {
		var participants = List.of(new ParticipantRequestDto(1L, "Mukhammadjon"), new ParticipantRequestDto(2L, "Akmal"), new ParticipantRequestDto(3L, "Sardor"));

		var items = List.of(
			new ItemDto("Osh", new BigDecimal("90000"), List.of(1L, 2L, 3L)),
			new ItemDto("Shashlik", new BigDecimal("35000"), List.of(1L)),
			new ItemDto("Lagman", new BigDecimal("28000"), List.of(2L)),
			new ItemDto("Manti", new BigDecimal("30000"), List.of(3L))
		);

		var request = new BillSplitRequestDto(participants, items, new BigDecimal("12.0"));
		var response = billService.split(request);

		var mkh = response.participantCalculations().get(0);
		assertEquals("Mukhammadjon", mkh.participantName());
		assertEquals(new BigDecimal("65000.00"), mkh.subtotal());
		assertEquals(new BigDecimal("7800.00"), mkh.serviceCharge());
		assertEquals(new BigDecimal("72800.00"), mkh.totalToPay());

		var akmal = response.participantCalculations().get(1);
		assertEquals("Akmal", akmal.participantName());
		assertEquals(new BigDecimal("58000.00"), akmal.subtotal());
		assertEquals(new BigDecimal("6960.00"), akmal.serviceCharge());
		assertEquals(new BigDecimal("64960.00"), akmal.totalToPay());

		var sardor = response.participantCalculations().get(2);
		assertEquals("Sardor", sardor.participantName());
		assertEquals(new BigDecimal("60000.00"), sardor.subtotal());
		assertEquals(new BigDecimal("7200.00"), sardor.serviceCharge());
		assertEquals(new BigDecimal("67200.00"), sardor.totalToPay());
	}
}