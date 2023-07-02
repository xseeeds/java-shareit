package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ShareItGatewayAppTest {

	@Test
	void main() {
		Assertions.assertDoesNotThrow(ShareItGatewayApp::new);
		Assertions.assertDoesNotThrow(() -> ShareItGatewayApp.main(new String[]{}));
	}
}
