package tech.marcosmartinelli.springsecurity.modules.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import tech.marcosmartinelli.springsecurity.exception.ResourceNotFoundException;
import tech.marcosmartinelli.springsecurity.modules.album.Album;
import tech.marcosmartinelli.springsecurity.modules.album.AlbumRepository;
import tech.marcosmartinelli.springsecurity.modules.image.dtos.ImagesByAlbumResponseDTO;

import java.nio.ByteBuffer;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;
    private final ImageRepository imageRepository;
    private final AlbumRepository albumRepository;

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
    }

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

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @albumSecurityService.isOwner(authentication, #albumId)")
    public void uploadMultipleFiles(List<MultipartFile> files, UUID albumId) {
        Album albumFromDb = this.albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com o ID: " + albumId));

        if (files.isEmpty()) {
            log.warn("Nenhum arquivo selecionado para upload.");
        }

        List<Image> uploadedFile = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                Image newImage = this.createImg(file, albumFromDb);
                uploadedFile.add(newImage);
            } catch (Exception e) {
                throw new IllegalArgumentException("\"Erro ao enviar arquivo \" + file.getOriginalFilename() + \": \" + e.getMessage()");
            }
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @albumSecurityService.isOwner(authentication, #albumId)")
    public List<ImagesByAlbumResponseDTO> findAllImagesByAlbumId(UUID albumId) {
        Album albumFromDb = this.albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com o ID: " + albumId));

        List<Image> imagesFromDb = this.imageRepository.findAllByAlbum(albumFromDb);

        List<ImagesByAlbumResponseDTO> imagesByAlbumResponseDTOList = new ArrayList<>();
        for (Image image : imagesFromDb) {
            try {
                ImagesByAlbumResponseDTO newImage = new ImagesByAlbumResponseDTO(
                        image.getImageId(),
                        image.getImgUrl(),
                        image.getFileName(),
                        image.getFileType(),
                        image.getFileSize()
                );
                imagesByAlbumResponseDTOList.add(newImage);
            } catch (Exception e) {
                throw new IllegalArgumentException("Erro ao buscar arquivo \" + image.getFileName() + \": \" + e.getMessage()");
            }
        }

        return imagesByAlbumResponseDTOList;
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @albumSecurityService.isOwner(authentication, #albumId)")
    public void deleteById(UUID albumId, UUID imageId){
        Image imageFromDb = this.imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagem não encontrada com o ID: " + imageId));

        UUID imgAlbumId = imageFromDb.getAlbum().getAlbumId();

        if(!imgAlbumId.equals(albumId)){
            throw new IllegalArgumentException("A imagem não pertence ao álbum especificado.");
        }

        this.deleteFromS3ByUrl(imageFromDb.getImgUrl());
        this.imageRepository.deleteById(imageId);
    }

    private void deleteFromS3ByUrl(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            if (key == null || key.isBlank()) {
                log.warn("Chave S3 não encontrada para URL: {}", fileUrl);
                return;
            }
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("Erro ao deletar objeto do S3: {}", e.getMessage());
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return null;
        }
        try {
            URI uri = URI.create(fileUrl);
            String path = uri.getPath();
            if (path == null || path.isBlank()) {
                return null;
            }
            String keyWithPossibleLeadingSlash = path.startsWith("/") ? path.substring(1) : path;
            return URLDecoder.decode(keyWithPossibleLeadingSlash, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            log.error("Erro ao extrair chave da URL do S3: {}", e.getMessage());
            return null;
        }
    }
}
