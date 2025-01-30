package com.example.fiap.videoslice.adapters.processor;

import com.example.fiap.videoslice.domain.processor.VideoProcessor;
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

public class VideoSliceProcessorImpl implements VideoProcessor {

    @Override
    public File videoSlice(Video video, int periodInSeconds) throws ApplicationException {
        System.out.println("VideoSliceProcessorImpl - videoSlice");
        try {
            Path tempDir = Files.createTempDirectory("frames");
            System.out.println("VideoSliceProcessorImpl - videoSlice 1");
            Integer time = LocalDateTime.now().getNano();

            System.out.println("VideoSliceProcessorImpl - videoSlice 2");
            String fileName = "frames_id_" + video.getId() + "__" + time + ".zip";

            System.out.println("VideoSliceProcessorImpl - videoSlice 3");
            File file = new File(tempDir.toFile(), fileName);
            System.out.println("VideoSliceProcessorImpl - videoSlice 4");
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file))) {
                System.out.println("VideoSliceProcessorImpl - videoSlice 5");
                extractFrames(video.getPath(), periodInSeconds, zipOutputStream);
            }
            System.out.println("VideoSliceProcessorImpl - videoSlice success");
            return file;

        }catch (IOException e){
            throw new ApplicationException("Error extracting the frames from the video." + e.getMessage());
        }
    }

    public void extractFrames(String videoFilePath, int periodInSeconds, ZipOutputStream zipOutputStream) throws IOException, ApplicationException {
        System.out.println("VideoSliceProcessorImpl - extractFrames 1");
        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFilePath)) {
            System.out.println("VideoSliceProcessorImpl - extractFrames 2");
            frameGrabber.start();
            System.out.println("VideoSliceProcessorImpl - extractFrames 3");
            Java2DFrameConverter converter = new Java2DFrameConverter();
            System.out.println("VideoSliceProcessorImpl - extractFrames 4");
            int frameNumber = 0;

            while (true) {
                Frame frame = frameGrabber.grabImage();
                if (frame == null) {
                    break;
                }
                System.out.println("VideoSliceProcessorImpl - extractFrames 5");
                BufferedImage bufferedImage = converter.convert(frame);
                if (bufferedImage == null) {
                    continue;
                }
                System.out.println("VideoSliceProcessorImpl - extractFrames 6");
                String fileName = "frame" + frameNumber++ + ".png";
                ZipEntry zipEntry = new ZipEntry(fileName);
                System.out.println("VideoSliceProcessorImpl - extractFrames 7");
                zipOutputStream.putNextEntry(zipEntry);

                System.out.println("VideoSliceProcessorImpl - extractFrames 8");
                ImageIO.write(bufferedImage, "png", zipOutputStream);
                System.out.println("VideoSliceProcessorImpl - extractFrames 9");
                zipOutputStream.closeEntry();
                System.out.println("VideoSliceProcessorImpl - extractFrames 10");
                frameGrabber.setTimestamp(frameGrabber.getTimestamp() + periodInSeconds * 1000000L);
            }
            System.out.println("VideoSliceProcessorImpl - extractFrames 11");
            frameGrabber.stop();
        } catch (Exception e) {
            System.out.println("VideoSliceProcessorImpl - Error extracting - " + e.toString());
            throw new ApplicationException("Error extracting the frames from the video. Could not open the file.");
        }
    }
}
