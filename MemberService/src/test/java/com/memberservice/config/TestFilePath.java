//package com.memberservice.config;
//
//import com.mysema.commons.lang.Assert;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//@ExtendWith(SpringExtension.class)
//@EnableConfigurationProperties(value = FilePath.class)
//@PropertySource(value = {"classpath:application.yml", "classpath:application-dev.yml", "classpath:application-local.yml"}, factory = YamlPropertySourceFactory.class)
//public class TestFilePath {
//
//    @Autowired
//    private FilePath filePath;
//
//    @Test
//    void test() {
//        Assertions.assertNotNull(filePath);
//        Assertions.assertEquals("C:/Users/gon/Pictures/test/", filePath.getNormal());
//    }
//}
