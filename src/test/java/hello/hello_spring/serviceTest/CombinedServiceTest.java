package hello.hello_spring.serviceTest;

import hello.hello_spring.service.CombinedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;

@SpringBootTest
public class CombinedServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CombinedServiceTest.class);

    @Autowired
    private CombinedService combinedService;

    @Test
    public void testProcessImageWithGPT() {
        File imageFile = new File("C:\\path\\to\\your\\test-image.png"); // 테스트 이미지 경로

        try {
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            String combinedResponse = combinedService.processImageWithGPT(imageBytes);
            logger.info("Combined Response: " + combinedResponse);
        } catch (Exception e) {
            logger.error("Error processing image with GPT", e);
        }
    }
}
