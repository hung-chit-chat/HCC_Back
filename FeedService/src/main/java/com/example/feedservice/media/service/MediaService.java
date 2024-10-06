package com.example.feedservice.media.service;

import com.example.feedservice.feed.entity.FeedEntity;
import com.example.feedservice.media.entity.MediaEntity;
import com.example.feedservice.media.repository.MediaRepository;
import com.example.feedservice.common.util.FeedUtil;
import com.example.feedservice.media.util.MediaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    private final FeedUtil feedUtil;

    private final MediaUtil mediaUtil;

    @Value("${upload.path}")
    private String uploadDir;

    /**
     * 파일 저장
     * @param feed, fileList - feedEntity, List<MultipartFile>
     * */
    public void uploadMediaAtStore(FeedEntity feed, List<MultipartFile> mediaList) throws IOException, RuntimeException, NoSuchAlgorithmException {

        List<MediaEntity> fileEntities = new ArrayList<>();

        Path directoryPath = Paths.get(uploadDir);
        if(!Files.exists(directoryPath)){
            Files.createDirectory(directoryPath);
        }

        // uploadDir/feed_id 로 폴더 생성
        Path savedDirectoryPath = Paths.get(directoryPath + "/" + feed.getFeedId());
        Files.createDirectory(savedDirectoryPath);


        // uploadDir/post_id 폴더에 파일 저장
        for (MultipartFile multipartFile : mediaList) {
            String mediaId = feedUtil.getUUID();
            String extension = "";

            /**
             * TODO :: 파일 MIME 타입 체크 메서드 리팩토링
             * */
            String contentType = multipartFile.getContentType();
            String mediaType = contentType.substring(0, 5);

            if (contentType == null || (
                    !contentType.equals("image/jpeg") &&
                            !contentType.equals("image/png") &&
                            !contentType.equals("image/gif") &&
                            !contentType.equals("image/jpg") &&
                            !contentType.equals("video/mp4") &&
                            !contentType.equals("video/quicktime")
            )) {
                // 지원하지 않는 형식이면 continue;
                continue;
            }

            extension = contentType.substring(contentType.lastIndexOf("/") + 1);

            String pathName = savedDirectoryPath + "/" + mediaId + "." + extension;
            multipartFile.transferTo(new File(pathName));

            String hashCode = mediaUtil.getHashGenerate(multipartFile, mediaType);

            MediaEntity mediaEntity = MediaEntity.builder()
                    .mediaId(mediaId)
                    .mediaPath(pathName)
                    .mediaSize(multipartFile.getSize())
                    .mediaName(multipartFile.getOriginalFilename())
                    .mediaHash(hashCode)
                    .build();

            // 연관관계 설정
            mediaEntity.setFeed(feed);

            fileEntities.add(mediaEntity);
        }

        mediaRepository.saveAll(fileEntities);

    }

    public void updateMediaAtStore(FeedEntity feed, List<MultipartFile> newMediaList)
            throws IOException, RuntimeException, NoSuchAlgorithmException {

        List<MediaEntity> fileEntities = new ArrayList<>();

        // 페치 조인으로 미디어 엔티티를 한번에 가져옴 N+1 문제 해결
        List<MediaEntity> oldMediaEntities = mediaRepository.findWithFeedByFeedId(feed.getFeedId()); // 페치 조인 적용

        Path savedDirectoryPath = Paths.get(uploadDir + "/" + feed.getFeedId());

        // 과거 미디어 데이터가 있으면 연관관계 초기화 및 폴더 비우기, 없으면 폴더 생성
        if(!oldMediaEntities.isEmpty()){

            mediaUtil.clearDirectory(savedDirectoryPath);   // 경로 폴더에 있는 파일 제거

            for (MediaEntity oldMediaEntity : oldMediaEntities) {
                oldMediaEntity.clearMediaAndFeed(feed);     // 연관관계 초기화
            }
                mediaRepository.deleteAllInBatch(oldMediaEntities);
        } else{
            // 폴더가 없으면 새로 생성
            if (!Files.exists(savedDirectoryPath)) {
                Files.createDirectories(savedDirectoryPath);
            }
        }

        if(newMediaList != null && !newMediaList.isEmpty()){
            for (MultipartFile multipartFile : newMediaList) {
                String contentType = multipartFile.getContentType();

                // 파일 타입 필터링
                if (contentType == null || (
                        !contentType.equals("image/jpeg") &&
                                !contentType.equals("image/png") &&
                                !contentType.equals("image/gif") &&
                                !contentType.equals("image/jpg") &&
                                !contentType.equals("video/mp4") &&
                                !contentType.equals("video/quicktime")
                )) {
                    continue;
                }

                // 파일 해시 생성
                String hashCode = mediaUtil.getHashGenerate(multipartFile, contentType.substring(0, 5));
                String mediaId = feedUtil.getUUID();

                String extension = multipartFile.getContentType().substring(multipartFile.getContentType().lastIndexOf("/") + 1);
                String pathName = savedDirectoryPath + "/" + mediaId + "." + extension;

                // 파일 저장
                multipartFile.transferTo(new File(pathName));

                // 미디어 엔티티 생성
                MediaEntity mediaEntity = MediaEntity.builder()
                        .mediaId(mediaId)
                        .mediaPath(pathName)
                        .mediaSize(multipartFile.getSize())
                        .mediaName(multipartFile.getOriginalFilename())
                        .mediaHash(hashCode)  // 해시값 저장
                        .build();

                // Feed 와의 연관관계 설정
                mediaEntity.setFeed(feed);
                fileEntities.add(mediaEntity);
            }

        }

        // 파일 한번에 저장
        if(!fileEntities.isEmpty()){
            mediaRepository.saveAll(fileEntities);
        }
    }
}
