package com.example.transactions.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("storage")
public class StorageProperties {

    public static String uploadDirLocation = "transactions/upload-dir";

}
