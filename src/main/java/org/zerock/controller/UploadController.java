package org.zerock.controller;

import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.domain.AttachFileDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@Log4j
public class UploadController {

    @GetMapping("/uploadForm")
    public void uploadForm() {
        log.info("upload form");
    }

    @PostMapping("/uploadFormAction")
    public void uploadFormPost(MultipartFile[] uploadFile, Model model) {

        String uploadFolder = "C:\\upload";

        for (MultipartFile multipartFile : uploadFile) {
            log.info("----------------------------------");
            log.info("Upload File Name: " + multipartFile.getOriginalFilename());
            log.info("Upload File Size: " + multipartFile.getSize());

            File saveFile = new File(uploadFolder, multipartFile.getOriginalFilename());

            try {
                multipartFile.transferTo(saveFile);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @GetMapping("/uploadAjax")
    public void uploadAjax() {
        log.info("upload ajax");
    }

    @PostMapping(value = "/uploadAjaxAction", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<List<AttachFileDTO>> uploadAjaxPost(MultipartFile[] uploadFile) {

        log.info("update ajax post...");

        List<AttachFileDTO> list = new ArrayList<>();
        String uploadFolder = "C:\\upload";

        String uploadFolderPath = getFolder();
        //make folder ----
        File uploadPath = new File(uploadFolder, uploadFolderPath);
        log.info("upload path: " + uploadPath);

        if (uploadPath.exists() == false) {
            uploadPath.mkdirs();
        }
        //make yyyy/mm/dd folder

        for (MultipartFile multipartFile : uploadFile) {

            AttachFileDTO attachFileDTO = new AttachFileDTO();

            String uploadFileName = multipartFile.getOriginalFilename();

            //IE has file path
            uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);
            log.info("only file name: " + uploadFileName);
            attachFileDTO.setFileName(uploadFileName);

            UUID uuid = UUID.randomUUID();
            uploadFileName = uuid.toString() + "_" + uploadFileName;

            try {
                File saveFile = new File(uploadFolder, uploadFileName);
                multipartFile.transferTo(saveFile);

                attachFileDTO.setUuid(uuid.toString());
                attachFileDTO.setUploadPath(uploadFolderPath);

                //check image type file
                if (checkImageType(saveFile)) {

                    attachFileDTO.setImage(true);

                    FileOutputStream thumbnail = new FileOutputStream(new File(uploadPath, "s_" + uploadFileName));
                    Thumbnailator.createThumbnail(multipartFile.getInputStream(), thumbnail, 100, 100);
                    thumbnail.close();
                }

                //add to List
                list.add(attachFileDTO);

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * 폴더 생성 메서드
     * @return
     */
    private String getFolder() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();

        String str = sdf.format(date);

        return str.replace("-", File.separator);
    }

    /**
     * 특정한 파일이 이미지 타입인지를 검사하는 메서드
     * @param file
     * @return
     */
    private boolean checkImageType(File file) {
        try {
            String contentType = Files.probeContentType(file.toPath());
            return contentType.startsWith("image");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
