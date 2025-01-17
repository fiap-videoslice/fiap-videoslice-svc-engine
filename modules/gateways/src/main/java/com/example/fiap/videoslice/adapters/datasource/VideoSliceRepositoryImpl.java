package com.example.fiap.videoslice.adapters.datasource;

import com.example.fiap.videoslice.domain.datasource.VideoDataSource;
import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class VideoSliceRepositoryImpl implements VideoDataSource {

    @Override
    public File videoSlice(Video video, int periodInSeconds) throws ApplicationException {

        try {
            Path tempDir = Files.createTempDirectory("frames");
            Integer time = LocalDateTime.now().getNano();

            String fileName = "frames_" + time + ".zip";

            File file = new File(tempDir.toFile(), fileName);
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file))) {
                extractFrames(video.getPath(), periodInSeconds, zipOutputStream);
            }
            return file;

        }catch (IOException e){
            throw new ApplicationException("Error extracting the frames from the video." + e.getMessage());
        }
    }

    public void extractFrames(String videoFilePath, int periodInSeconds, ZipOutputStream zipOutputStream) throws IOException, ApplicationException {

        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFilePath)) {
            frameGrabber.start();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            int frameNumber = 0;

            while (true) {
                Frame frame = frameGrabber.grabImage();
                if (frame == null) {
                    break;
                }

                BufferedImage bufferedImage = converter.convert(frame);
                if (bufferedImage == null) {
                    continue;
                }

                String fileName = "frame" + frameNumber++ + ".png";
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOutputStream.putNextEntry(zipEntry);
                ImageIO.write(bufferedImage, "png", zipOutputStream);
                zipOutputStream.closeEntry();

                frameGrabber.setTimestamp(frameGrabber.getTimestamp() + periodInSeconds * 1000000L);
            }

            frameGrabber.stop();
        } catch (Exception e) {
            throw new ApplicationException("Error extracting the frames from the video. Could not open the file.");
        }
    }
}
