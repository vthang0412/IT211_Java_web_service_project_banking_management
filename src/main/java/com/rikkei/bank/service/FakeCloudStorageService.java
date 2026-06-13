package com.rikkei.bank.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class    FakeCloudStorageService
        implements CloudStorageService {

    @Override
    public String upload(
            MultipartFile file
    ) {

        return "https://cloud.fake/"
                + file.getOriginalFilename();
    }
}