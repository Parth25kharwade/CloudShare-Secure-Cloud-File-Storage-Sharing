package com.parth.cloudshare.service;

import com.parth.cloudshare.Documents.FileMetadataDocument;
import com.parth.cloudshare.Documents.ProfileDocument;
import com.parth.cloudshare.dto.FileMetadataDto;
import com.parth.cloudshare.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileMetadataService {
    private final ProfileService profileService;
    private  final UserCreditService userCreditService;
    private final FileMetadataRepository fileMetadataRepository;

    public List<FileMetadataDto> uploadFile(MultipartFile[] files) throws IOException {
      ProfileDocument currentProfile=profileService.getCurrentProfile();
      List<FileMetadataDocument> savedFiles=new ArrayList<>();
      if(!userCreditService.hasEnoughCredits(files.length)){
          throw new RuntimeException("Not Enough Credits..Please Purchase More Credits");

      }
        Path uploadPaths=Paths.get("uploads").toAbsolutePath().normalize();
        Files.createDirectories(uploadPaths);
        for(MultipartFile file:files){
            String fileName=UUID.randomUUID()+"."+ StringUtils.getFilenameExtension(file.getOriginalFilename());
            Path targetLoc=uploadPaths.resolve(fileName);
            Files.copy(file.getInputStream(),targetLoc, StandardCopyOption.REPLACE_EXISTING);

            FileMetadataDocument fileMetadataDocument=FileMetadataDocument.builder()
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .size(file.getSize())
                    .clerkId(currentProfile.getClerkId())
                    .isPublic(false)
                    .fileLocation(targetLoc.toString())
                    .uploadedAt(LocalDateTime.now())
                    .build();

            // TODO : Consume i creadt for each file
            userCreditService.consumeCredits();
            savedFiles.add(fileMetadataRepository.save(fileMetadataDocument));


        }
       return savedFiles.stream().map(fileMetadataDocument -> mapToDTO(fileMetadataDocument)).collect(Collectors.toList());
    }

    private FileMetadataDto mapToDTO(FileMetadataDocument fileMetadataDocument) {
       return FileMetadataDto.builder()
                .id(fileMetadataDocument.getId())
                .name(fileMetadataDocument.getName())
                .type(fileMetadataDocument.getType())
                .size(fileMetadataDocument.getSize())
                .clerkId(fileMetadataDocument.getClerkId())
                .isPublic(fileMetadataDocument.isPublic())
                .fileLocation(fileMetadataDocument.getFileLocation())
                .uploadedAt(fileMetadataDocument.getUploadedAt())
                .build();

    }
    public List<FileMetadataDto> getFiles(){
        ProfileDocument currentProfile=profileService.getCurrentProfile();
        List<FileMetadataDocument> files=fileMetadataRepository.findByClerkId(currentProfile.getClerkId());
        return files.stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    public FileMetadataDto getPublicFile(String id){
        Optional<FileMetadataDocument> fileOptional=fileMetadataRepository.findById(id);
        if(fileOptional.isEmpty() || !fileOptional.get().isPublic()){
            throw new RuntimeException("Unable to get the file");
        }
        FileMetadataDocument fileMetadataDocument=fileOptional.get();
        return mapToDTO(fileMetadataDocument);
    }
    public FileMetadataDto getDownloadableFile(String id){
        FileMetadataDocument fileMetadataDocument=fileMetadataRepository.findById(id).orElseThrow(() -> new RuntimeException("Unable to get the file"));
        return mapToDTO(fileMetadataDocument);
    }
    public void deleteFile(String id){
        try {
            ProfileDocument currentProfile=profileService.getCurrentProfile();
            FileMetadataDocument file= fileMetadataRepository.findById(id).orElseThrow(() -> new RuntimeException("Unable to get the file"));
            if(!file.getClerkId().equals(currentProfile.getClerkId())){
                throw new RuntimeException("This File is not belongs to current user");
            }

            Path path=Paths.get(file.getFileLocation());
            Files.deleteIfExists(path);
            fileMetadataRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file....");
        }

    }
    public FileMetadataDto togglePublic(String id){
        FileMetadataDocument file=fileMetadataRepository.findById(id).orElseThrow(()-> new RuntimeException("Unable to get the file"));
        file.setPublic(!file.isPublic());
        fileMetadataRepository.save(file);
        return mapToDTO(file);
    }
}
