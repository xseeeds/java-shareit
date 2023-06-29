package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.repository.BookingShortDtoProjection;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.expception.exp.BadRequestException;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

	@InjectMocks
    private BookingServiceImpl bookingService;

	@Mock
	private BookingRepository bookingRepository;

	private UserService userService;
	private ItemService itemService;

	private ItemEntity itemEntity;
	private BookingRequestDto bookingRequestDto;
	private BookingEntity bookingEntity;
	private BookingShortResponseDto lastBookingShort;
	private BookingShortResponseDto nextBookingShort;
	private BookingShortDtoProjection lastBookingShortDtoProjection;
	private BookingShortDtoProjection nextBookingShortDtoProjection;

	@BeforeEach
	void setUp() {

		userService = mock(UserService.class);
		itemService = mock(ItemService.class);

		ReflectionTestUtils
				.setField(bookingService, "userService", userService);
		ReflectionTestUtils
				.setField(bookingService, "itemService", itemService);

		final UserEntity ownerEntity = UserEntity
				.builder()
				.id(1L)
				.name("Owner")
				.email("owner@ya.ru")
				.build();

		final UserEntity bookerEntity = UserEntity
				.builder()
				.id(2L)
				.name("Booker")
				.email("booker@ya.ru")
				.build();

		itemEntity = ItemEntity
				.builder()
				.id(1L)
				.name("Item A")
				.description("Description A")
				.available(true)
				.ownerId(ownerEntity.getId())
				.owner(ownerEntity)
				.build();

		bookingRequestDto = BookingRequestDto
				.builder()
				.itemId(itemEntity.getId())
				.start(LocalDateTime.now().minusDays(2))
				.end(LocalDateTime.now().minusDays(1))
				.build();

		bookingEntity = BookingEntity
				.builder()
				.start(LocalDateTime.now().minusDays(2))
				.end(LocalDateTime.now().minusDays(1))
				.itemId(itemEntity.getId())
				.item(itemEntity)
				.bookerId(bookerEntity.getId())
				.booker(bookerEntity)
				.status(Status.WAITING)
				.build();

		lastBookingShort = BookingShortResponseDto
				.builder()
				.bookerId(1L)
				.start(LocalDateTime.now().minusDays(2))
				.end(LocalDateTime.now().minusDays(1))
				.build();

		nextBookingShort = BookingShortResponseDto
				.builder()
				.bookerId(1L)
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.build();
		lastBookingShortDtoProjection = new BookingShortDtoProjection() {
			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Long getBookerId() {
				return lastBookingShort.getBookerId();
			}

			@Override
			public LocalDateTime getStart() {
				return lastBookingShort.getStart();
			}

			@Override
			public LocalDateTime getEnd() {
				return lastBookingShort.getEnd();
			}
		};

		nextBookingShortDtoProjection = new BookingShortDtoProjection() {
			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Long getBookerId() {
				return nextBookingShort.getBookerId();
			}

			@Override
			public LocalDateTime getStart() {
				return nextBookingShort.getStart();
			}

			@Override
			public LocalDateTime getEnd() {
				return nextBookingShort.getEnd();
			}
		};

	}

	@Test
	void createBookingWhenItemIsAvailableAndBookerIsNotOwnerThenBookingReturnTest() {
		doNothing()
				.when(userService)
				.checkUserIsExistById(anyLong());

		when(itemService
				.findItemEntityById(anyLong()))
				.thenReturn(itemEntity);
		when(bookingRepository
				.save(any(BookingEntity.class)))
				.then(returnsFirstArg());
		final BookingResponseDto bookingResponseDto = bookingService
				.createBooking(2L, bookingRequestDto);

		assertThat(bookingResponseDto.getStatus(),
				equalTo(Status.WAITING));
		verify(bookingRepository, times(1))
				.save(any(BookingEntity.class));
	}


	@Test
	void createBookingWhenItemIsNotAvailableThenBadRequestExceptionTest() {
		when(itemService
				.findItemEntityById(anyLong()))
				.thenReturn(ItemEntity
						.builder()
						.available(false)
						.build());

		assertThrows(BadRequestException.class,
				() -> bookingService.createBooking(2L, bookingRequestDto));
	}

	@Test
	void createBookingWhenBookerIsOwnerItemThenNotFoundExceptionTest() {
		when(itemService
				.findItemEntityById(anyLong()))
				.thenReturn(itemEntity);

		assertThrows(NotFoundException.class,
				() -> bookingService.createBooking(1L, bookingRequestDto));
	}

	@Test
	void approveBookingByIdWhenUserIsOwnerAndStatusIsWaitingThenBookingApprovedReturnTest() {
		when(bookingRepository
				.findByIdAndItemOwnerId(anyLong(), anyLong()))
				.thenReturn(bookingEntity);

		when(bookingRepository
				.save(any(BookingEntity.class)))
				.then(returnsFirstArg());
		final BookingResponseDto bookingResponseDto = bookingService
				.approveBookingById(1L, 1L, true);

		assertThat(bookingResponseDto.getStatus(), equalTo(Status.APPROVED));

		verify(bookingRepository, times(1))
				.findByIdAndItemOwnerId(anyLong(), anyLong());
		verify(bookingRepository, times(1))
				.save(any(BookingEntity.class));
	}

	@Test
	void approveBookingByIdWhenUserIsNotOwnerThenNotFoundExceptionTest() {
		when(bookingRepository
				.findByIdAndItemOwnerId(anyLong(), anyLong()))
				.thenReturn(null);

		assertThrows(NotFoundException.class,
				() -> bookingService.approveBookingById(1L, 2L, true));
	}

	@Test
	void approveBookingByIdWhenStatusIsNotWaitingThenBadRequestExceptionTest() {
		final BookingEntity bookingApproved = bookingEntity
				.toBuilder()
				.status(Status.APPROVED)
				.build();

		when(bookingRepository
				.findByIdAndItemOwnerId(anyLong(), anyLong()))
				.thenReturn(bookingApproved);

		assertThrows(BadRequestException.class,
				() -> bookingService.approveBookingById(1L, 1L, true));
	}

	@Test
	void findBookingByIdWhenUserIsOwnerAndBookingFoundThenBookingReturnedTest() {
		when(bookingRepository
				.findByIdAndBookerIdOrItemOwnerId(anyLong(), anyLong(), anyLong()))
				.thenReturn(bookingEntity);

		assertNotNull(bookingService.findBookingById(1L, 2L));
	}

	@Test
	void findBookingByIdWhenUserIsNeitherOwnerNorBooker_thenResourceNotFoundExceptionThrown() {
		when(bookingRepository
				.findByIdAndBookerIdOrItemOwnerId(anyLong(), anyLong(), anyLong()))
				.thenReturn(null);

		assertThrows(NotFoundException.class,
				() -> bookingService.findBookingById(1L, 3L));
	}


	@Test
	void findBookingsWhenNoBookingsThenEmptyListReturnTest() {
		doNothing()
				.when(userService)
				.checkUserIsExistById(anyLong());
		when(bookingRepository
				.findAll(any(Predicate.class), any(PageRequest.class)))
				.thenReturn(Page.empty());

		bookingService.findBookings(1, "ALL", 0, 10, false);
		bookingService.findBookings(1, "CURRENT", 0, 10, false);
		bookingService.findBookings(1, "PAST", 0, 10, false);
		bookingService.findBookings(1, "FUTURE", 0, 10, false);
		bookingService.findBookings(1, "REJECTED",0, 10, false);
		bookingService.findBookings(1, "WAITING", 0, 10, false);

		bookingService.findBookings(2, "ALL", 0, 10, true);
		bookingService.findBookings(2, "CURRENT", 0, 10, true);
		bookingService.findBookings(2, "PAST", 0, 10, true);
		bookingService.findBookings(2, "FUTURE", 0, 10, true);
		bookingService.findBookings(2, "REJECTED",0, 10, true);
		bookingService.findBookings(2, "WAITING", 0, 10, true);

	}

	@Test
	void checkExistsByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApprovedTest() {
		bookingService.checkExistsByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApproved(1L, 2L);

		verify(bookingRepository, times(1))
				.existsByItemIdAndBookerIdAndEndIsBeforeAndStatus(
						anyLong(), anyLong(), any(LocalDateTime.class), any(Status.class));
	}

	@Test
	void findLastBookingTest() {
		when(bookingRepository
				.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
						anyLong(), any(LocalDateTime.class), any(Status.class)))
				.thenReturn(lastBookingShortDtoProjection);

		final BookingShortResponseDto lastBookingShortDto = bookingService.findLastBooking(1L, LocalDateTime.now());

		assertThat(lastBookingShortDto.getBookerId(), equalTo(lastBookingShort.getBookerId()));
		assertThat(lastBookingShortDto.getStart(), equalTo(lastBookingShort.getStart()));
		assertThat(lastBookingShortDto.getEnd(), equalTo(lastBookingShort.getEnd()));

		verify(bookingRepository, times(1))
				.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
						anyLong(), any(LocalDateTime.class), any(Status.class));
	}

	@Test
	void findNextBookingTest() {
		when(bookingRepository
				.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
						anyLong(), any(LocalDateTime.class), any(Status.class)))
				.thenReturn(nextBookingShortDtoProjection);

		final BookingShortResponseDto nextBookingShortDto = bookingService.findNextBooking(1L, LocalDateTime.now());

		assertThat(nextBookingShortDto.getBookerId(), equalTo(nextBookingShort.getBookerId()));
		assertThat(nextBookingShortDto.getStart(), equalTo(nextBookingShort.getStart()));
		assertThat(nextBookingShortDto.getEnd(), equalTo(nextBookingShort.getEnd()));

		verify(bookingRepository, times(1))
				.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
						anyLong(), any(LocalDateTime.class), any(Status.class));
	}

	@Test
	void findAllByItemOwnerId() {
		bookingService.findAllBookingByItemOwnerId(1L);

		verify(bookingRepository, times(1))
				.findAllBookingByItemOwnerId(anyLong());
	}

}