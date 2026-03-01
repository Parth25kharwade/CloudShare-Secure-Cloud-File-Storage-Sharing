package com.parth.cloudshare.controller;

import com.parth.cloudshare.Documents.UserCredit;
import com.parth.cloudshare.dto.FileMetadataDto;
import com.parth.cloudshare.service.FileMetadataService;
import com.parth.cloudshare.service.UserCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileMetadataService fileMetadataService;
    private final UserCreditService userCreditService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestPart("files") MultipartFile[] files) throws IOException {
        Map<String,Object> responce=new HashMap<>();
        List<FileMetadataDto> list=fileMetadataService.uploadFile(files);
        UserCredit finalCredits=userCreditService.getUserCredits();

        responce.put("files",list);
        responce.put("remainingCredits",finalCredits.getCredits());
        return ResponseEntity.ok(responce);

    }
    @GetMapping("/my")
    public ResponseEntity<?> getFilesForCurrenUsers(){
        Map<String,Object> responce=new HashMap<>();
        List<FileMetadataDto> list=fileMetadataService.getFiles();
        UserCredit finalCredits=userCreditService.getUserCredits();

        responce.put("files",list);
        responce.put("remainingCredits",finalCredits.getCredits());
        return ResponseEntity.ok(responce);
    }
    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPublicFile(@PathVariable String id){
        Map<String,Object> responce=new HashMap<>();
        FileMetadataDto fileMetadataDto=fileMetadataService.getPublicFile(id);

        responce.put("file",fileMetadataDto);
        return ResponseEntity.ok(responce);
    }
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> getDownloadableFile(@PathVariable String id) throws IOException {

        FileMetadataDto downloadableFile=fileMetadataService.getDownloadableFile(id);
        Path path=Paths.get(downloadableFile.getFileLocation());
        Resource resource=new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=\""+downloadableFile.getName()+"\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id){
        fileMetadataService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/toggle-public/{id}")
    public ResponseEntity<?> togglePublic(@PathVariable String id){
        Map<String,Object> responce=new HashMap<>();
        FileMetadataDto fileMetadataDto=fileMetadataService.togglePublic(id);
        responce.put("file",fileMetadataDto);
        return ResponseEntity.ok(responce);
    }



}
