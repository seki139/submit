package com.example.demo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UploadFormData {
	
    private List<MultipartFile> files;
	
    private List<String> punctuation;
	
    private String formatting;

    private MultipartFile file;
    private String password;
    
    
    private String search;
   
}
