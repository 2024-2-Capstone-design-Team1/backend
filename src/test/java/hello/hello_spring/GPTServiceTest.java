// 위치: src/test/java/hello/hello_spring/GPTServiceTest.java

package hello.hello_spring;

import hello.hello_spring.DTO.GPTRequest;
import hello.hello_spring.DTO.GPTResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
public class GPTServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(GPTServiceTest.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gpt.api.url}")
    private String apiUrl;

    @Value("${gpt.model}")
    private String model;

    @Test
    public void testChatGPTAPI() {
        // 1. GPTRequest 생성 (임의의 입력을 사용해 ChatGPT를 테스트)
        String prompt = "Hello, ChatGPT! Can you respond to this?";
        GPTRequest gptRequest = new GPTRequest(
                model,
                prompt,
                1,
                256,
                1,
                0,
                0
        );

        try {
            // 2. GPT API 호출
            GPTResponse gptResponse = restTemplate.postForObject(
                    apiUrl,
                    gptRequest,
                    GPTResponse.class
            );

            // 3. GPT 응답 결과 로그 출력
            if (gptResponse != null && !gptResponse.getChoices().isEmpty()) {
                String gptOutput = gptResponse.getChoices().get(0).getMessage().getContent();
                logger.info("GPT Output: " + gptOutput);
            } else {
                logger.info("GPT Response was empty or null.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
