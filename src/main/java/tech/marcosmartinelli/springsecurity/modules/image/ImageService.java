package tech.marcosmartinelli.springsecurity.modules.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import tech.marcosmartinelli.springsecurity.modules.album.Album;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;
    private final ImageRepository imageRepository;

    public Image createImg(MultipartFile file, Album album) {

        String imgUrl = this.uploadImg(file);

        Image newImage = new Image();
        newImage.setAlbum(album);
        newImage.setDateFile(LocalDateTime.now());
        newImage.setFileName(file.getOriginalFilename());
        newImage.setFileSize(file.getSize());
        newImage.setFileType(file.getContentType());
        newImage.setImgUrl(imgUrl);

        imageRepository.save(newImage);

        return newImage;
    };

    public String uploadImg(MultipartFile multipartFile) {
        String fileName = UUID.randomUUID() + multipartFile.getOriginalFilename();
        try {
            String contentType = multipartFile.getContentType();

            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .build();
            s3Client.putObject(putOb, RequestBody.fromByteBuffer(ByteBuffer.wrap(multipartFile.getBytes())));
            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            return s3Client.utilities().getUrl(request).toString();
        } catch (Exception e) {
            log.error("erro ao subir arquivo: {}", e.getMessage());
            return "";
        }
    }
}
