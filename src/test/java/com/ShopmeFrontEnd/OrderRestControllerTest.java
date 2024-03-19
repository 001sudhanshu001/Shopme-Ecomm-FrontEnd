package com.ShopmeFrontEnd;

import com.ShopmeFrontEnd.dto.OrderReturnRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithUserDetails("learningpath305@gmail.com")
    public void testSendOrderReturnRequestFailed() {
        Integer orderId = 1111;
        OrderReturnRequest request = new OrderReturnRequest(orderId, "", "");

        String requestURL = "/order/return";

//        mockMvc.perform(post(requestURL)
//                .with(csrf())
//                .contentType("application/json")
//                .content(requestURL)
//        );
    }
}
