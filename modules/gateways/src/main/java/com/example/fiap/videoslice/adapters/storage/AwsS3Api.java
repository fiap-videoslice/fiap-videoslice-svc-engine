package com.example.fiap.videoslice.adapters.storage;

import com.example.fiap.videoslice.adapters.messaging.AwsClientUtils;
import com.example.fiap.videoslice.domain.exception.ApplicationException;
import com.example.fiap.videoslice.domain.processor.VideoFileStoreDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AwsS3Api implements VideoFileStoreDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AwsS3Api.class);

    private String s3Endpoint;
    private String bucketName;
    private S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Autowired
    public AwsS3Api(Environment environment) {
        this.s3Endpoint = environment.getProperty("videoslice.integration.s3.s3Endpoint");
        this.bucketName = environment.getProperty("videoslice.integration.s3.bucketName");

        this.s3Endpoint = Objects.requireNonNull(s3Endpoint, "videoslice.integration.s3.s3Endpoint not set");
        this.bucketName = Objects.requireNonNull(bucketName, "videoslice.integration.s3.bucketName not set");


        S3ClientBuilder builder = S3Client.builder()
                .region(Region.US_EAST_1);

        builder = AwsClientUtils.maybeOverrideEndpoint(builder, environment);

        s3Client = builder.build();

        this.s3Presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .build();
    }


//    /**
//     * Performs a multipart upload to Amazon S3 using the provided S3 client.
//     *
//     * @param fileParam the path to the file to be uploaded
//     */
//    public String saveFileMultiPart(File fileParam) throws ApplicationException {
////    public void multipartUploadWithS3Client(String filePath) {
//        String filePath = fileParam.getPath();
//        String framesFilePath;
//        try {
//
//            System.out.println("AwsS3Api - CreateMultipartUploadResponse - 1");
//            // Initiate the multipart upload.
//            CreateMultipartUploadResponse createMultipartUploadResponse = s3Client.createMultipartUpload(b -> b
//                    .bucket(bucketName)
//                    .key(fileParam.getName()));
//
//            CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
//                    .bucket(bucketName)
//                    .key(fileParam.getName())
//                    .build();
//
//            System.out.println("AwsS3Api - CreateMultipartUploadResponse - 2");
//            String uploadId = createMultipartUploadResponse.uploadId();
//
//            // Upload the parts of the file.
//            int partNumber = 1;
//            List<CompletedPart> completedParts = new ArrayList<>();
//            ByteBuffer bb = ByteBuffer.allocate(1024 * 1024 * 5); // 5 MB byte buffer
//
//            System.out.println("AwsS3Api - CreateMultipartUploadResponse - 3");
//            try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
//                long fileSize = file.length();
//                long position = 0;
//                System.out.println("AwsS3Api - CreateMultipartUploadResponse - 4 fileSize - " + fileSize);
//                while (position < fileSize) {
//                    file.seek(position);
//                    System.out.println("AwsS3Api - while - " + position);
//                    long read = file.getChannel().read(bb);
//
//                    System.out.println("AwsS3Api - b4 bb.flip()");
//                    bb.flip(); // Swap position and limit before reading from the buffer.
//
//                    System.out.println("AwsS3Api - b4 uploadPartRequest");
//                    UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
//                            .bucket(bucketName)
//                            .key("key" + position)
//                            .uploadId(uploadId)
//                            .partNumber(partNumber)
//                            .build();
//
//                    System.out.println("AwsS3Api - b4 UploadPartResponse");
//                    UploadPartResponse partResponse = s3Client.uploadPart(
//                            uploadPartRequest,
//                            RequestBody.fromByteBuffer(bb));
//
//                    System.out.println("AwsS3Api - b4 CompletedPart");
//                    CompletedPart part = CompletedPart.builder()
//                            .partNumber(partNumber)
//                            .eTag(partResponse.eTag())
//                            .build();
//
//                    System.out.println("AwsS3Api - b4 add");
//                    completedParts.add(part);
//
//                    System.out.println("AwsS3Api - b4 clear");
//                    bb.clear();
//                    position += read;
//                    partNumber++;
//                }
//
//            } catch (IOException e) {
//                System.out.println("AwsS3Api - erro - " + e.toString());
//                throw new ApplicationException("Error uploading the file to the S3 bucket");
//            }
//
//            System.out.println("AwsS3Api - b4 s3Client.completeMultipartUpload");
//            // Complete the multipart upload.
//            s3Client.completeMultipartUpload(b -> b
//                    .bucket(bucketName)
//                    .key(fileParam.getName())
//                    .uploadId(uploadId)
//                    .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build()));
//
//            System.out.println("AwsS3Api - getBucketFullPath() - " + getBucketFullPath());
//            framesFilePath = getBucketFullPath() + "/" + fileParam.getName();
//            System.out.println("AwsS3Api - framesFilePath - " + framesFilePath);
//
//            return framesFilePath;
//
//        }catch(S3Exception e){
//            System.out.println("AwsS3Api - erro - " + e.toString());
//            throw new ApplicationException("Error uploading the file to the S3 bucket");
//        }
//    }


    @Override
    public String saveFile(File file) throws ApplicationException {
        String framesFilePath;

        try {
            System.out.println("AwsS3Api - saveFile");
            System.out.println("AwsS3Api bucketName - " + bucketName);
            System.out.println("AwsS3Api file.getName() - " + file.getName());
            System.out.println("AwsS3Api file.toPath() - " + file.toPath());


            if (file.exists()) {
                System.out.println("O arquivo existe.");
            } else {
                System.out.println("O arquivo não existe.");
            }


            Path path = file.toPath();

            System.out.println("AwsS3Api - pre request body");
            RequestBody requestBody = RequestBody.fromFile(path);

            LOGGER.info("Saving file {} with {} bytes to bucket {}");

            System.out.println("AwsS3Api - pre putObjectRequest");

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.getName())
                    .contentType("application/zip")
                    .contentLength(file.length())
                    .build();
            System.out.println("AwsS3Api - pre presignedRequest");

            // Gerar a URL pré-assinada
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r -> r
                    .signatureDuration(Duration.ofMinutes(120))
                    .putObjectRequest(putObjectRequest));

            System.out.println("AwsS3Api - pre putObject");

            // Obter a URL pré-assinada
            String presignedUrl = presignedRequest.url().toString();

            System.out.println("Presigned URL generated: {} - " + presignedUrl);


            // Fazer o upload do arquivo usando a URL pré-assinada
            uploadFileUsingPresignedUrl(presignedUrl, file);

            framesFilePath = getBucketFullPath() + "/" + file.getName();
            LOGGER.info("File uploaded to {}", framesFilePath);
            return framesFilePath;

//            return presignedUrl;

            //Original
//            PutObjectResponse response = s3Client.putObject(putObjectRequest, requestBody);


//            s3Client.putObject(
//                    PutObjectRequest.builder()
//                            .bucket(bucketName)
//                            .key(file.getName())
//                            .build(), file.toPath());

//            System.out.println("AwsS3Api - getBucketFullPath() - " + getBucketFullPath());
//            framesFilePath = getBucketFullPath() + "/" + file.getName();
//            System.out.println("AwsS3Api - framesFilePath - " + framesFilePath);
        } catch (IOException e) {
            System.out.println("AwsS3Api - erro - " + e.toString());
            System.out.println("AwsS3Api - erro - " + e);
            System.out.println("AwsS3Api - erro - " + e.getMessage());
            System.out.println("AwsS3Api - erro - " + e.getStackTrace().toString());
            throw new ApplicationException("Error uploading the file to the S3 bucket");

        } catch (S3Exception e) {
            System.out.println("AwsS3Api - erro - " + e.toString());
            System.out.println("AwsS3Api - erro - " + e);
            System.out.println("AwsS3Api - erro - " + e.getMessage());
            System.out.println("AwsS3Api - erro - " + e.getStackTrace().toString());
            throw new ApplicationException("Error uploading the file to the S3 bucket");
        }

//        return framesFilePath;
    }


    private void uploadFileUsingPresignedUrl(String presignedUrl, File file) throws IOException {
        // Usar HttpURLConnection ou HttpClient para fazer o upload do arquivo usando a URL pré-assinada
        java.net.URL url = new java.net.URL(presignedUrl);
        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/zip");

        try (java.io.OutputStream outputStream = connection.getOutputStream()) {
            Files.copy(file.toPath(), outputStream);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to upload file: HTTP response code " + responseCode);
        }
    }


    public String getBucketFullPath() {

        Pattern patternHttps = Pattern.compile("(https://)");
        Matcher matcherHttps = patternHttps.matcher(s3Endpoint);

        Pattern patternHttp = Pattern.compile("(http://)");
        Matcher matcherHttp = patternHttp.matcher(s3Endpoint);

        String bucketFullPath;

        if (matcherHttps.find()) {
            bucketFullPath = matcherHttps.replaceFirst("$1" + bucketName + ".");
        } else if (matcherHttp.find()) {
            bucketFullPath = s3Endpoint + "/" + bucketName;
        } else {
            bucketFullPath = s3Endpoint;
        }

        return bucketFullPath;
    }

    @Override
    public void deleteFile(String filePath) {
        S3Client s3 = S3Client.builder()
                .region(Region.of(Region.US_EAST_1.toString()))
                .endpointOverride(URI.create(s3Endpoint))
//                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();

        String fileName = extractFileNameFromPath(filePath);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3.deleteObject(deleteObjectRequest);

        LOGGER.info("File {} deleted from bucket {}", fileName, bucketName);
    }

    private String extractFileNameFromPath(String filePath) {
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }
}
