package hello.hello_spring.serviceTest;

import hello.hello_spring.service.VisionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;

@SpringBootTest
public class VisionServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(VisionServiceTest.class);

    @Autowired
    private VisionService visionService;

    @Test
    public void testExtractTextFromImage() {
        File imageFile = new File("C:\\path\\to\\your\\test-image.png"); // 테스트 이미지 경로

        try {
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            String extractedText = visionService.extractTextFromImage(imageBytes);
            logger.info("Extracted Text: " + extractedText);
        } catch (Exception e) {
            logger.error("Error extracting text from image", e);
        }
    }
}
