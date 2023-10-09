package com.example.todolist.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.todolist.common.Utils;
import com.example.todolist.entity.AttachedFile;
import com.example.todolist.repository.AttachedFileRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DownloadService {
    private final AttachedFileRepository attachedFileRepository;

    @Value("${attached.file.path}")
    private String ATTACHED_FILE_PATH;

    public void donwloadAttachedFile(int afId, HttpServletResponse response) {
        AttachedFile af = attachedFileRepository.findById(afId).get();
        String fileName = af.getFileName();
        String fext = fileName.substring(fileName.lastIndexOf(".") + 1);
        String contentType = Utils.ext2contentType(fext);
        String downLoadFilePath = Utils.makeAttahcedFilePath(ATTACHED_FILE_PATH, af);
        File donwloadFile = new File(downLoadFilePath);

        BufferedInputStream bis = null;
        OutputStream out = null;

        try {
            if (contentType.equals("")) {
                response.setContentType("application/force-download");
                response.setHeader("Content-Disposition",
                        "attachment;filename=\"" + URLEncoder.encode(af.getFileName(), "UTF-8") + "\"");
            } else {
                response.setContentType(contentType);
                response.setHeader("Content-Disposition", "inline");
            }
            response.setContentLengthLong(donwloadFile.length());

            bis = new BufferedInputStream(new FileInputStream(downLoadFilePath));
            out = response.getOutputStream();
            byte[] buff = new byte[8 * 1024];
            int nRead = 0;
            while ((nRead = bis.read(buff)) != -1) {
                out.write(buff, 0, nRead);
            }
            out.flush();
            bis.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
