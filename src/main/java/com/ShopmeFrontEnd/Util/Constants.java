package com.ShopmeFrontEnd.Util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Constants {
    public static final String S3_BASE_URI = getS3BaseUri();

    private static String getS3BaseUri() {
        String bucketName = "shopme305";
        String region = "ap-south-1";
        String pattern = "https://%s.s3.%s.amazonaws.com";

        return String.format(pattern, bucketName, region);
    }

}
