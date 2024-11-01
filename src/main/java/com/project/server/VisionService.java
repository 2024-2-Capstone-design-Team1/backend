package com.project.server;

import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Service
public class VisionService {
    private static final Logger logger = LoggerFactory.getLogger(VisionService.class);

    public String extractTextFromImage(byte[] imageBytes) throws IOException {
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            ByteString imgBytes = ByteString.copyFrom(imageBytes);

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            List<AnnotateImageRequest> requests = List.of(request);

            TextAnnotation text = vision.batchAnnotateImages(requests)
                    .getResponses(0)
                    .getFullTextAnnotation();

            String extractedText = text.getText();
            logger.info("Extracted Text: {}", extractedText); // 추출된 텍스트 로그 출력

            return text.getText();
        }
    }
}
