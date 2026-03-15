package com.myapp.backend.service;

import java.io.InputStream;

public interface StorageService {

    String upload(String containerName, String blobName, InputStream data, long length, String contentType);

    InputStream download(String containerName, String blobName);

    void delete(String containerName, String blobName);

    String getUri(String containerName, String blobName);
}
