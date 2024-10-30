package hello.hello_spring.serviceTest;


import hello.hello_spring.service.GPTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
public class GPTServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(GPTServiceTest.class);

    @Autowired
    private GPTService gptService;

    @Test
    public void testGetGPTResponse() {
        String prompt = "안전한 일반 의약품에 대해 알려줘.";

        try {
            String gptResponse = gptService.getGPTResponse(prompt);
            logger.info("GPT Response: " + gptResponse);
        } catch (Exception e) {
            logger.error("Error getting GPT response", e);
        }
    }
}
