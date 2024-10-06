package com.example.feedservice.media.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class MediaUtil {

    /**
     * 파일을 Hash 값으로 변경해서 리턴한다
     * @param multipartFile - 멀티파트 파일
     * @param mediaType - image,video 에 따라 배열 크기 달라짐
     * @return String - Hash 값
     * */
    public String getHashGenerate(MultipartFile multipartFile, String mediaType) throws NoSuchAlgorithmException, IOException {

        // SHA-256 해시 알고리즘을 사용
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        //
        File convertedFile = this.multipartFileToFile(multipartFile);

        FileInputStream fis = new FileInputStream(convertedFile);

        byte[] byteArray = new byte[8192];   // 8KB 크기

        if("vidio".equals(mediaType)){
            byteArray = new byte[131072];    //  비디오면 128KB
        }

        int bytesRead = 0;

        // 정해진 크기로 파일을 읽음
        while ((bytesRead = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesRead);
        }
        fis.close();

        // 해시값 추출
        byte[] hashBytes = digest.digest();

        // 해시값을 16진수 문자열로 변환
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    /**
     * Convert MultipartFile -> File
     * @param multipartFile - 멀티파트 파일
     * @return file
     * */
    public File multipartFileToFile(MultipartFile multipartFile) throws IOException {

        File convFile = new File(multipartFile.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();

        return convFile;

    }

    /**
     * 경로에 있는 모든 파일 지우기
     * */
    public void clearDirectory(Path directoryPath) throws IOException {
        if (Files.exists(directoryPath)) {
            // 폴더 내부의 파일들을 삭제 (폴더 자체는 삭제하지 않음)
            Files.walk(directoryPath)
                    .filter(Files::isRegularFile)  // 파일만 선택
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}