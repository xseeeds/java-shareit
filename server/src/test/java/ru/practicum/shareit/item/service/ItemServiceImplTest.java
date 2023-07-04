package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.exp.BadRequestException;
import ru.practicum.shareit.exception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.CommentEntity;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.ItemShortResponseDtoProjection;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.repository.UserNameProjection;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;

    private UserService userService;
    private BookingService bookingService;

    private UserEntity requesterEntity;
    private UserNameProjection authorName;
    private ItemRequestDto itemRequestDto;
    private ItemShortResponseDtoProjection itemShortResponseDtoProjection;
    private ItemEntity itemEntity;
    private CommentEntity commentEntity;
    private BookingShortResponseDto lastBookingShort;
    private BookingShortResponseDto nextBookingShort;
    private BookingEntity lastBookingEntity;
    private BookingEntity nextBookingEntity;
    private CommentRequestDto commentRequestDto;
    private Page<ItemEntity> itemEntityPage;

    @BeforeEach
    void setUp() {

        userService = mock(UserService.class);
        bookingService = mock(BookingService.class);

        ReflectionTestUtils
                .setField(itemService, "userService", userService);
        ReflectionTestUtils
                .setField(itemService, "bookingService", bookingService);


        requesterEntity = UserEntity
                .builder()
                .id(1L)
                .name("Requester")
                .email("Requester@ya.ru")
                .build();

        final UserEntity ownerEntity = UserEntity
                .builder()
                .id(2L)
                .name("Owner")
                .email("owner@ya.ru")
                .build();

        authorName = () -> requesterEntity.getName();

        final ItemRequestEntity itemRequestEntity = ItemRequestEntity
                .builder()
                .description("Item is needed")
                .requesterId(requesterEntity.getId())
                .requester(requesterEntity)
                .created(LocalDateTime.now())
                .build();

        itemRequestDto = ItemRequestDto
                .builder()
                .name("Item")
                .description("")
                .available(true)
                .requestId(1L)
                .build();

        itemShortResponseDtoProjection = new ItemShortResponseDtoProjection() {
            @Override
            public Long getId() {
                return null;
            }

            @Override
            public String getName() {
                return itemRequestDto.getName();
            }

            @Override
            public String getDescription() {
                return itemRequestDto.getDescription();
            }

            @Override
            public Boolean getAvailable() {
                return itemRequestDto.getAvailable();
            }

            @Override
            public Long getRequestId() {
                return itemRequestDto.getRequestId();
            }

        };

        itemEntity = ItemEntity.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .ownerId(ownerEntity.getId())
                .owner(ownerEntity)
                .requestId(itemRequestEntity.getId())
                .request(itemRequestEntity)
                .build();

        commentEntity = CommentEntity
                .builder()
                .text("Comment for item")
                .itemId(itemEntity.getId())
                .item(itemEntity)
                .authorId(requesterEntity.getId())
                .author(requesterEntity)
                .created(LocalDateTime.now())
                .build();

        lastBookingShort = BookingShortResponseDto
                .builder()
                .bookerId(requesterEntity.getId())
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        nextBookingShort = BookingShortResponseDto
                .builder()
                .bookerId(requesterEntity.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        lastBookingEntity = BookingEntity
                .builder()
                .itemId(itemEntity.getId())
                .item(itemEntity)
                .bookerId(requesterEntity.getId())
                .booker(requesterEntity)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(Status.APPROVED)
                .build();

        nextBookingEntity = BookingEntity
                .builder()
                .itemId(itemEntity.getId())
                .item(itemEntity)
                .bookerId(requesterEntity.getId())
                .booker(requesterEntity)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.APPROVED)
                .build();

        commentRequestDto = CommentRequestDto
                .builder()
                .text("Comment")
                .build();

        itemEntityPage = new PageImpl<>(singletonList(itemEntity));
    }

    @Test
    void createItemWhenOwnerIsExistsThenItemResponseDtoReturnTest() {
        doNothing()
                .when(userService)
                .checkUserIsExistById(anyLong());

        when(itemRepository
                .save(any(ItemEntity.class)))
                .then(returnsFirstArg());

        final ItemResponseDto createdItemResponseDto = itemService.createItem(2L, itemRequestDto);

        assertThat(createdItemResponseDto.getName(), equalTo(itemRequestDto.getName()));

        verify(itemRepository, times(1))
                .save(any(ItemEntity.class));
    }

    @Test
    void createItemWhenOwnerNotFoundThenNotFoundExceptionTest() {
        doThrow(NotFoundException.class)
                .when(userService).checkUserIsExistById(anyLong());

        assertThrows(NotFoundException.class, () -> itemService
                .createItem(2L, itemRequestDto));
        verify(itemRepository, never())
                .save(any(ItemEntity.class));
    }

    @Test
    void findItemWithBookingAndCommentsResponseDtoByIdWhenItemFoundAndUserIsOwnerThenItemWithBookingAndCommentsResponseDtoReturnTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemEntity));
        when(commentRepository
                .findAllByItemIdOrderByCreatedDesc(anyLong(), any(Pageable.class)))
                .thenReturn(singletonList(commentEntity));
        when(bookingService
                .findLastBooking(anyLong(), any(LocalDateTime.class)))
                .thenReturn(lastBookingShort);
        when(bookingService
                .findNextBooking(anyLong(), any(LocalDateTime.class)))
                .thenReturn(nextBookingShort);

        final ItemWithBookingAndCommentsResponseDto itemDto = itemService
                .findItemWithBookingAndCommentsResponseDtoById(2L, 1L, 0, 10);

        assertThat(itemDto.getName(), equalTo(itemEntity.getName()));
        assertThat(itemDto.getComments().size(), equalTo(1));
        assertThat(itemDto.getComments().get(0).getText(), equalTo(commentEntity.getText()));
        assertNotNull(itemDto.getLastBooking());
        assertNotNull(itemDto.getNextBooking());
    }

    @Test
    void findItemWithBookingAndCommentsResponseDtoByIdWhenItemFoundAndUserIsNotOwnerThenItemWithBookingAndCommentsResponseDtoReturnTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemEntity));
        when(commentRepository
                .findAllByItemIdOrderByCreatedDesc(anyLong(), any(Pageable.class)))
                .thenReturn(singletonList(commentEntity));

        final ItemWithBookingAndCommentsResponseDto itemDto = itemService
                .findItemWithBookingAndCommentsResponseDtoById(1L, 1L, 0, 10);

        assertThat(itemDto.getName(), equalTo(itemEntity.getName()));
        assertThat(itemDto.getComments().size(), equalTo(1));
        assertThat(itemDto.getComments().get(0).getText(), equalTo(commentEntity.getText()));
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    void findItemWithBookingAndCommentsResponseDtoByIdWhenItemNotFoundThenNotFoundExceptionTest() {
        doThrow(NotFoundException.class)
                .when(itemRepository)
                .findById(anyLong());

        assertThrows(NotFoundException.class, () -> itemService
                .findItemWithBookingAndCommentsResponseDtoById(1L, 1L, 0, 10));
    }


    @Test
    void updateItemWhenUserIsOwnerThenItemResponseDtoReturnTest() {
        final ItemRequestDto updatedItemResponseDto = itemRequestDto
                .toBuilder()
                .name("Item Update")
                .description("Description Update")
                .available(false)
                .build();

        when(itemRepository
                .findByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(Optional.of(itemEntity));

        when(itemRepository.save(any())).then(returnsFirstArg());
        ItemResponseDto returnItemResponseDto = itemService
                .updateItem(2L, 1L, updatedItemResponseDto);

        assertThat(returnItemResponseDto.getName(), equalTo(updatedItemResponseDto.getName()));
        assertThat(returnItemResponseDto.getDescription(), equalTo(updatedItemResponseDto.getDescription()));
        assertFalse(returnItemResponseDto.getAvailable());

        verify(itemRepository, times(1))
                .save(any(ItemEntity.class));
    }

    @Test
    void updateItemWhenUserIsNotOwnerThenItemResponseDtoReturnTest() {
        final ItemRequestDto updatedItemResponseDto = itemRequestDto
                .toBuilder()
                .name("Item Update")
                .description("Description Update")
                .available(false)
                .build();

        doThrow(NotFoundException.class)
                .when(itemRepository)
                .findByIdAndOwnerId(anyLong(), anyLong());

        assertThrows(NotFoundException.class, () -> itemService
                .updateItem(2L, 1L, updatedItemResponseDto));

        verify(itemRepository, never())
                .save(any(ItemEntity.class));
    }

    @Test
    void deleteItemByIdWhenUserIsOwnerThenItemDeleteTest() {
        when(itemRepository
                .existsByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(true);

        itemService.deleteItemById(2L, 1L);

        verify(itemRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    void deleteItemByIdWhenUserIsNotOwnerThenItemNotDeleteTest() {
        when(itemRepository
                .existsByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(false);

       assertThrows(NotFoundException.class, () -> itemService
               .deleteItemById(2L, 1L));

        verify(itemRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void findItemWithBookingAndCommentsResponseDtoByOwnerIdThenReturnItemWithBookingAndCommentsResponseDtoListTest() {
        doNothing()
                .when(userService)
                .checkUserIsExistById(anyLong());
        when(bookingService
                .findAllBookingByItemOwnerId(anyLong()))
                .thenReturn(List.of(lastBookingEntity, nextBookingEntity));
        when(itemRepository
                .findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(itemEntityPage);
        when(commentRepository.findAllByItemIdIn(anyList()))
                .thenReturn(List.of(commentEntity));

        final List<ItemWithBookingAndCommentsResponseDto> itemDtoList = itemService
                .findItemWithBookingAndCommentsResponseDtoByOwnerId(2L, 0, 10);

        assertThat(itemDtoList.size(), equalTo(1));
        assertThat(itemDtoList.get(0).getName(), equalTo(itemEntity.getName()));
        assertThat(itemDtoList.get(0).getComments().size(), equalTo(1));
        assertThat(itemDtoList.get(0).getComments().get(0).getText(), equalTo(commentEntity.getText()));
    }

    @Test
    void findItemIsAvailableByNameOrDescriptionWhenFoundThenItemResponseDtoListReturn() {
        when(itemRepository
                .findItemIsAvailableByNameOrDescription(anyString(), any(Pageable.class)))
                .thenReturn(itemEntityPage);
        final List<ItemResponseDto> itemResponseDtoList = itemService
                .findItemIsAvailableByNameOrDescription("text", 0, 10);

        assertThat(itemResponseDtoList.size(), equalTo(1));
        assertThat(itemResponseDtoList.get(0).getName(), equalTo(itemEntity.getName()));
        assertThat(itemResponseDtoList.get(0).getDescription(), equalTo(itemEntity.getDescription()));
        assertThat(itemResponseDtoList.get(0).getAvailable(), equalTo(itemEntity.getAvailable()));
    }

    @Test
    void createCommentWhenExistsBookingByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApprovedThenCommentResponseDtoReturnTest() {
        when(bookingService
                .checkExistsByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApproved(anyLong(), anyLong()))
                .thenReturn(true);
        when(userService
                .findNameByUserId(anyLong()))
                .thenReturn(authorName);
        when(commentRepository
                .save(any(CommentEntity.class)))
                .then(returnsFirstArg());

        final CommentResponseDto commentResponseDto = itemService.createComment(1L, 1L, commentRequestDto);

        assertThat(commentResponseDto.getText(), equalTo(commentRequestDto.getText()));
        assertThat(commentResponseDto.getAuthorName(), equalTo(requesterEntity.getName()));

        verify(commentRepository, times(1))
                .save(any(CommentEntity.class));
    }

    @Test
    void createCommentWhenNotExistsBookingByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApprovedThenBadRequestExceptionTest() {
        when(bookingService
                .checkExistsByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApproved(anyLong(), anyLong()))
                .thenReturn(false);

        assertThrows(BadRequestException.class, () -> itemService
                .createComment(1L, 1L, commentRequestDto));

        verify(commentRepository, never())
                .save(any(CommentEntity.class));
    }

    @Test
    void findItemShortResponseDtoByRequestIdInTest() {
        when(itemRepository
                .findAllByRequestIdIn(anyList()))
                .thenReturn(List.of(itemShortResponseDtoProjection));
        final List<ItemShortResponseDto> itemShortResponseDtoList = itemService
                .findItemShortResponseDtoByRequestIdIn(List.of(1L));

        assertThat(itemShortResponseDtoList.size(), equalTo(1));
        assertThat(itemShortResponseDtoList.get(0).getName(), equalTo(itemShortResponseDtoProjection.getName()));
        assertThat(itemShortResponseDtoList.get(0).getDescription(), equalTo(itemShortResponseDtoProjection.getDescription()));
        assertThat(itemShortResponseDtoList.get(0).getAvailable(), equalTo(itemShortResponseDtoProjection.getAvailable()));

        verify(itemRepository, times(1))
                .findAllByRequestIdIn(anyList());
    }

    @Test
    void findItemEntityByIdWhenUserFoundThenUserReturnTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemEntity));

        final ItemEntity foundItemEntity = itemService.findItemEntityById(1L);


        assertThat(foundItemEntity.getName(), equalTo(itemEntity.getName()));
        assertThat(foundItemEntity.getDescription(), equalTo(itemEntity.getDescription()));
        assertThat(foundItemEntity.getAvailable(), equalTo(itemEntity.getAvailable()));

        verify(itemRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void findItemEntityByIdWhenUserNotFoundThenNotFoundExceptionTest() {
        doThrow(BadRequestException.class)
                .when(itemRepository)
                .findById(anyLong());

        assertThrows(BadRequestException.class, () -> itemService
                .findItemEntityById(1L));

        verify(itemRepository, times(1))
                .findById(anyLong());
    }

    @Test
    public void checkItemIsExistByIdThenReturnTrueTest() {
        when(itemRepository
                .existsById(anyLong()))
                .thenReturn(true);

        itemService.checkItemIsExistById(1L);

        verify(itemRepository, times(1))
                .existsById(anyLong());
    }

    @Test
    public void checkItemIsExistByIdThenReturnFalseTest() {
        when(itemRepository
                .existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService
                .checkItemIsExistById(1L));

        verify(itemRepository, times(1))
                .existsById(anyLong());
    }

}