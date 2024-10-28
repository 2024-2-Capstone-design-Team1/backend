package hello.hello_spring;

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
    public void testExtractTextFromLocalImage() {
        File imageFile = new File("C:\\Users\\aji00\\OneDrive\\사진\\스크린샷\\스크린샷 2024-10-28 135746.png");  // 로컬 이미지 경로를 지정합니다.

        try {
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            String extractedText = visionService.extractTextFromImage(imageBytes);
            logger.info("Extracted Text: " + extractedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
