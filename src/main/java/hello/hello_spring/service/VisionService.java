package hello.hello_spring.service;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class VisionService {

    // MultipartFile로 이미지를 받아서 텍스트를 추출하는 메서드
    public String extractTextFromImage(MultipartFile file) {
        try {
            byte[] imageBytes = file.getBytes();
            return extractTextFromImage(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error extracting text from image.";
        }
    }

    // byte[]로 이미지를 받아서 텍스트를 추출하는 메서드 오버로드
    public String extractTextFromImage(byte[] imageBytes) {
        try {
            ByteString imgBytes = ByteString.copyFrom(imageBytes);

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                StringBuilder stringBuilder = new StringBuilder();
                for (AnnotateImageResponse res : response.getResponsesList()) {
                    if (res.hasError()) {
                        System.out.printf("Error: %s\n", res.getError().getMessage());
                        return "Error detected in OCR.";
                    }
                    stringBuilder.append(res.getFullTextAnnotation().getText());
                }
                return stringBuilder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing image with OCR.";
        }
    }
}
