package com.myapp.backend.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.myapp.backend.service.StorageService;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class AzureBlobStorageServiceImpl implements StorageService {

    private final BlobServiceClient blobServiceClient;

    public AzureBlobStorageServiceImpl(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    @Override
    public String upload(String containerName, String blobName, InputStream data, long length, String contentType) {
        BlobContainerClient container = getOrCreateContainer(containerName);
        BlobClient blob = container.getBlobClient(blobName);
        blob.upload(data, length, true);
        blob.setHttpHeaders(new com.azure.storage.blob.models.BlobHttpHeaders().setContentType(contentType));
        return blob.getBlobUrl();
    }

    @Override
    public InputStream download(String containerName, String blobName) {
        BlobContainerClient container = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blob = container.getBlobClient(blobName);
        return blob.openInputStream();
    }

    @Override
    public void delete(String containerName, String blobName) {
        BlobContainerClient container = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blob = container.getBlobClient(blobName);
        blob.deleteIfExists();
    }

    @Override
    public String getUri(String containerName, String blobName) {
        BlobContainerClient container = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blob = container.getBlobClient(blobName);
        return blob.getBlobUrl();
    }

    private BlobContainerClient getOrCreateContainer(String containerName) {
        BlobContainerClient container = blobServiceClient.getBlobContainerClient(containerName);
        if (!container.exists()) {
            container.create();
        }
        return container;
    }
}
