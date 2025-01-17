package com.example.fiap.videoslice.adapters.datasource;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class VIdeoSlicer {

    public static void main(String[] args) {

        String diretorioAtual = new File("").getAbsolutePath();
        String videoUrl = diretorioAtual + File.separator + "video.mp4";
        Integer captureInterval = 60;

        try {
            File file = new File(videoUrl);
            String s3Url = extractFramesToS3(videoUrl, captureInterval);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error processing video");
        }

    }

    public static String extractFramesToS3(String videoFilePath, int periodInSeconds) throws IOException {
//        Path tempDir = Files.createTempDirectory("frames");
        String diretorioAtual = new File("").getAbsolutePath();

//        File video = new File(tempDir.toFile(), videoFile.getOriginalFilename());
//        videoFile.transferTo(video);

        File zipFile = new File(diretorioAtual, "frames.zip");
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
//            extractFrames(video.getAbsolutePath(), periodInSeconds, zipOutputStream);
            extractFrames(videoFilePath, periodInSeconds, zipOutputStream);
        }

//        S3Client s3 = S3Client.builder()
//                .region(Region.of(region))
//                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
//                .build();
//
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key("frames.zip")
//                .build();
//
//        s3.putObject(putObjectRequest, zipFile.toPath());
//
//        return "https://" + bucketName + ".s3.amazonaws.com/frames.zip";
        return "ok";
    }

    private static void extractFrames(String videoFilePath, int periodInSeconds, ZipOutputStream zipOutputStream) throws IOException {
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
            e.printStackTrace();
        }
    }

}
