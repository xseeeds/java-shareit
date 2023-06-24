package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.util.Util;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {

    private final TestEntityManager em;
    private final ItemRepository repository;

    @Test
    @Transactional
    void findItemIsAvailableByNameOrDescriptionTest() {
        final ItemEntity itemEntityNameTextPersist = em.persist(ItemEntity
                .builder()
                .name("ContainsText")
                .description("Description")
                .available(true)
                .build());
        final ItemEntity itemEntityDescriptionTextPersist = em.persist(ItemEntity
                .builder()
                .name("Name")
                .description("TextContains")
                .available(true)
                .build());
        em.persist(ItemEntity
                .builder()
                .name("Item")
                .description("Item")
                .available(true)
                .build());
        em.persist(ItemEntity
                .builder()
                .name("ContainsText")
                .description("Description")
                .available(false)
                .build());

        em.persist(ItemEntity
                .builder()
                .name("Name")
                .description("TextContains")
                .available(false)
                .build());

        em.flush();

        final List<ItemEntity> foundItemEntity = repository
                .findItemIsAvailableByNameOrDescription("text", Util.getPageSortAscByProperties(0, 10, "id"))
                .getContent();

        assertThat(foundItemEntity, hasSize(2));
        assertThat(foundItemEntity, hasItems(itemEntityNameTextPersist, itemEntityDescriptionTextPersist));
    }

}