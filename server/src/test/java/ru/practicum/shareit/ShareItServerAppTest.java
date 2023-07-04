package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ShareItServerAppTest {

	@Test
	void main() {
		Assertions.assertDoesNotThrow(ShareItServerApp::new);
		Assertions.assertDoesNotThrow(() -> ShareItServerApp.main(new String[]{}));
	}
}
