package com.rikkei.bank.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudStorageService {

    String upload(
            MultipartFile file
    );

}