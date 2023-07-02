package ru.practicum.shareit.request.service;

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
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Mock
    private ItemRequestRepository requestRepository;

    private UserService userService;
    private ItemService itemService;

    private ItemRequestEntity itemRequestEntity;
    private ItemShortResponseDto itemShortResponseDto;
    private Page<ItemRequestEntity> itemRequestEntityPage;
    private ItemRequestResponseDto itemRequestResponseDto;
    private ItemRequestRequestDto itemRequestDto;


    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        itemService = mock(ItemService.class);

        ReflectionTestUtils
                .setField(requestService, "userService", userService);
        ReflectionTestUtils
                .setField(requestService, "itemService", itemService);

        final UserEntity requesterEntity = UserEntity
                .builder()
                .id(1L)
                .name("requester")
                .email("requester@ya.ru")
                .build();

        itemRequestEntity = ItemRequestEntity
                .builder()
                .id(1L)
                .description("Request for item")
                .requesterId(requesterEntity.getId())
                .requester(requesterEntity)
                .created(LocalDateTime.now())
                .build();

        final UserEntity userEntity = UserEntity
                .builder()
                .id(2L)
                .name("User")
                .email("user@ya.ru")
                .build();

        final ItemEntity itemEntity = ItemEntity
                .builder()
                .name("Item")
                .description("Description")
                .available(true)
                .ownerId(userEntity.getId())
                .owner(userEntity)
                .requestId(itemRequestEntity.getId())
                .request(itemRequestEntity)
                .build();

        itemShortResponseDto = ItemShortResponseDto
                .builder()
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.getAvailable())
                .requestId(itemEntity.getRequestId())
                .build();

        itemRequestEntityPage = new PageImpl<>(singletonList(itemRequestEntity));

        itemRequestResponseDto = ItemRequestResponseDto
                .builder()
                .id(itemRequestEntity.getId())
                .description(itemRequestEntity.getDescription())
                .requester(UserResponseDto
                        .builder()
                        .id(requesterEntity.getId())
                        .name(requesterEntity.getName())
                        .email(requesterEntity.getEmail())
                        .build())
                .created(itemRequestEntity.getCreated())
                .items(List.of(itemShortResponseDto))
                .build();

        itemRequestDto = ItemRequestRequestDto
                .builder()
                .description("Request for item")
                .build();
    }

    @Test
    void createItemRequestWhenRequesterFoundThenRequestReturnTest() {
        doNothing()
                .when(userService)
                .checkUserIsExistById(anyLong());
        when(requestRepository
                .save(any(ItemRequestEntity.class)))
                .then(returnsFirstArg());

        final ItemRequestResponseDto itemRequestResponseDto = requestService
                .createItemRequest(1L, itemRequestDto);

        assertThat(itemRequestResponseDto.getDescription(), equalTo(itemRequestDto.getDescription()));

        verify(requestRepository, times(1))
                .save(any(ItemRequestEntity.class));
    }

    @Test
    void findItemRequestByIdWhenRequestFoundThenRequestReturnTest() {
        doNothing()
                .when(userService)
                .checkUserIsExistById(anyLong());
        when(requestRepository
                .findById(anyLong()))
                .thenReturn(Optional.of(itemRequestEntity));
        when(itemService
                .findItemShortResponseDtoByRequestIdIn(anyList()))
                .thenReturn(List.of(itemShortResponseDto));

        final ItemRequestResponseDto itemRequestResponseDto = requestService.findItemRequestById(1L, 1L);

        assertThat(itemRequestResponseDto.getDescription(), equalTo(itemRequestEntity.getDescription()));
    }

    @Test
    void findOwnerItemRequestWhenRequesterFoundThenItemRequestResponseDtoListReturnTest() {
        doNothing()
                .when(userService)
                .checkUserIsExistById(anyLong());
        when(requestRepository
                .findAllByRequesterIdOrderByCreatedDesc(anyLong(), any(Pageable.class)))
                .thenReturn(itemRequestEntityPage);
        when(itemService.findItemShortResponseDtoByRequestIdIn(anyList()))
                .thenReturn(List.of(itemShortResponseDto));

        final List<ItemRequestResponseDto> itemRequestResponseDtoList = requestService
                .findOwnerItemRequest(1L, 0, 10);

        assertThat(itemRequestResponseDtoList.size(), equalTo(1));
        assertThat(itemRequestResponseDtoList, hasItems(itemRequestResponseDto));
    }

    @Test
    void findOthersItemRequestWhenUserFoundThenItemRequestResponseDtoListReturnTest() {
        doNothing()
                .when(userService)
                .checkUserIsExistById(anyLong());
        when(requestRepository
                .findAllByRequesterIdIsNotOrderByCreatedDesc(anyLong(), any(Pageable.class)))
                .thenReturn(itemRequestEntityPage);
        when(itemService.findItemShortResponseDtoByRequestIdIn(anyList()))
                .thenReturn(List.of(itemShortResponseDto));

        final List<ItemRequestResponseDto> itemRequestResponseDtoList = requestService
                .findOthersItemRequest(2L, 0, 10);

        assertThat(itemRequestResponseDtoList.size(), equalTo(1));
        assertThat(itemRequestResponseDtoList, hasItems(itemRequestResponseDto));
    }

}