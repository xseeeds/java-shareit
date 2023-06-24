package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.model.UserEntity;

import java.time.LocalDateTime;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {

	private final TestEntityManager em;
	private final BookingRepository repository;

	@Test
	@Transactional
	void findByIdAndBookerIdOrItemOwnerIdTest() {
		final UserEntity ownerPersist = em.persist(UserEntity
				.builder()
				.name("Owner")
				.email("owner@ya.ru")
				.build());
		final ItemEntity itemPersist = em.persist(ItemEntity
				.builder()
				.name("Item")
				.description("Item")
				.available(true)
				.owner(ownerPersist)
				.build());
		final UserEntity bookerPersist = em.persist(UserEntity
				.builder()
				.name("Booker")
				.email("booker@ya.ru")
				.build());
		final BookingEntity bookingEntity = BookingEntity
				.builder()
				.itemId(itemPersist.getId())
				.bookerId(bookerPersist.getId())
				.status(Status.APPROVED)
				.start(LocalDateTime.now().minusDays(2))
				.end(LocalDateTime.now().minusDays(1))
				.build();
		final BookingEntity bookingPersist = em.persist(bookingEntity);

		em.flush();

		final BookingEntity foundBookingEntity = repository
				.findByIdAndBookerIdOrItemOwnerId(
						bookingPersist.getId(), bookerPersist.getId(), ownerPersist.getId());
		final BookingEntity notFoundBookingEntity = repository
				.findByIdAndBookerIdOrItemOwnerId(
						bookingPersist.getId(), ownerPersist.getId(), bookerPersist.getId());

		assertThat(foundBookingEntity, equalTo(bookingEntity));
		assertThat(notFoundBookingEntity, nullValue(BookingEntity.class));
	}

}