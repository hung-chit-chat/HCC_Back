package com.example.feedservice.media.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class MediaUtil {

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
