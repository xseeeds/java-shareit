package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@UtilityClass
public class Util {

    public PageRequest getPageSortAscByProperties(int from, int size, String properties) {
        return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.ASC, properties));
    }

    public PageRequest getPageSortDescByProperties(int from, int size, String properties) {
        return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, properties));
    }
}
