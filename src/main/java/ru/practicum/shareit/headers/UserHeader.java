package ru.practicum.shareit.headers;

/*
import org.springframework.http.HttpEntity;
import java.lang.annotation.Annotation;

public class MyHttpHeaders extends HttpEntity<Long> implements Annotation {

    public MyHttpHeaders(Long body) {
        super(body);
    }

    public MyHttpHeaders(long headerValue) {
        super(headerValue);
    }

    public Long value() {
        return getBody();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return MyHttpHeader.class;
    }
}
*/

/*

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;


@Getter
@Setter
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
public class MyHttpHeader extends HttpEntity<Long> implements Annotation {

    private final HttpHeaders headers = new HttpHeaders();
    private final String headerName;

    public MyHttpHeader(String headerName, long headerValue) {
        super(headerValue);
        this.headerName = headerName;
        headers.add(headerName, String.valueOf(headerValue));
    }

    public long value() {
        return getBody();
    }

    public long getUserId() {
        String headerValue = headers.getFirst(headerName);
        return Long.parseLong(headerValue);
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return MyHttpHeader.class;
    }
}
*/


/*
@Getter
@Setter
@RequiredArgsConstructor
@Component
public class MyHttpHeaders extends HttpEntity<Long> implements MyHttpHeader {

    private final HttpHeaders headers = new HttpHeaders();      //?

    public MyHttpHeaders(String headerName, long headerValue) {
        super(headerValue);
        headers.add(headerName, String.valueOf(headerValue));
    }

    public long value() {
        return getBody();
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
*/


/*
@PostMapping
public ItemDto createItem(@MyHttpHeader(name = "userId") long userId, @RequestBody ItemDtoCreate itemDto) {
        return itemService.createItem(userId, itemDto);
        }

public class MyHttpHeaders extends HttpEntity<Long> implements Annotation {
    public MyHttpHeaders(String headerName, long headerValue) {
        super(headerValue);
        headers.add(headerName, String.valueOf(headerValue));
    }

    public long value() {
        return getBody();
    }
}

@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyHttpHeader {
    String name() default "";
}

*/


/*
@PostMapping
public ItemDto createItem(@MyHttpHeader long userId, @RequestBody ItemDtoCreate itemDto) {
        return itemService.createItem(userId, itemDto);
        }


public class MyHttpHeaders extends HttpEntity<Long> implements Annotation {
    public MyHttpHeaders(Long headerValue) {
        super(headerValue);
    }

    public Long value() {
        return getBody();
    }
}

@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyHttpHeader {}
*/


/*
    @PostMapping
    public ItemDto createItem(@MyHttpHeader String userId, @RequestBody ItemDtoCreate itemDto) {
        return itemService.createItem(userId, itemDto);
    }

public class MyHttpHeaders extends HttpEntity<String> implements Annotation {

    public MyHttpHeaders(String headerName) {
        super(headerName);
    }

    public String value() {
        return getBody();
    }
}

@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyHttpHeader {
}
*/


/*

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

import java.util.Map;


@Getter
@Setter
@RequiredArgsConstructor
public class MyHttpHeaders extends HttpHeaders {

    private final Map<String, Object> headers;

}
*/


/*

    В данном примере мы наследуем класс HttpHeaders и добавляем свое собственное поле myCustomHeader.
        Также определяем соответствующие геттеры и сеттеры для этого поля.

        Затем мы можем использовать этот заголовок в своих методах, например:


        import org.springframework.http.HttpEntity;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @PostMapping("/custom")
    public void processRequest(@RequestBody String requestBody, MyHttpHeaders headers ) {
        System.out.println("Request body: " + requestBody);
        System.out.println("Custom header: " + headers.getMyCustomHeader());
    }
}


        Обратите внимание, что мы передаем объект MyHttpHeaders в качестве аргумента метода processRequest.
        Мы можем использовать это объект, чтобы получить значение поля myCustomHeader, переданное в заголовке HTTP.
*/



/*
    Для создания своего заголовка можно создать отдельный класс, отнаследованный от HttpHeaders,
    и добавить в него нужное поле. Далее, в методе контроллера, можно использовать аннотацию @RequestHeader
    для получения значения заголовка.
    Например, создадим класс UserHeader:
*/

/*
public class UserHeader extends HttpHeaders {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    public UserHeader() { super(); } // вызываем конструктор суперкласса
    // public void setUserId(long userId) { this.set(X_SHARER_USER_ID, String.valueOf(userId)); }
    // public long getUserId() {
    // List<String> values = this.get(X_SHARER_USER_ID);
    // if (values == null || values.isEmpty()) {
    // return 0;
    // }
    // return Long.parseLong(values.get(0));
    // }
    // }
}
*/


import org.springframework.http.HttpHeaders;
import java.util.List;


public class UserHeader extends HttpHeaders {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    public void setUserId(long userId) {
        this.set(X_SHARER_USER_ID, String.valueOf(userId));
    }

    public long getUserId() {
        List<String> values = this.get(X_SHARER_USER_ID);
        if (values == null || values.isEmpty()) {
            return -1;
        }
        return Long.parseLong(values.get(0));
    }
}

/*
        Здесь мы добавили поле X_SHARER_USER_ID, а также методы setUserId и getUserId для удобства работы с заголовком.

        Далее, в методе контроллера можно использовать этот заголовок:


@PatchMapping("/{itemId}")
public ItemDto updateItem(@PathVariable long itemId, @RequestHeader UserHeader headers, @RequestBody ItemDto itemDto) {
        long userId = headers.getUserId();

        }
*/