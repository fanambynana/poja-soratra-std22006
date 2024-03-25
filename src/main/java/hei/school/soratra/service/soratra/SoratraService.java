package hei.school.soratra.service.soratra;

import hei.school.soratra.endpoint.rest.controller.soratra.SoratraData;
import hei.school.soratra.file.BucketComponent;
import hei.school.soratra.file.FileHash;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Optional;

import static hei.school.soratra.file.FileHashAlgorithm.NONE;
import static java.io.File.createTempFile;
import static java.nio.file.Files.createTempDirectory;
import static java.util.UUID.randomUUID;

@Service
public class SoratraService {
    BucketComponent bucketComponent;

    public SoratraData saveById(int id, String poeticSentence) throws IOException {
        String ORIGINAL_KEY = "ogl";
        String TRANSFORMED_KEY = "mfd";

        var fileSuffix = ".txt";
        var originalFilePrefix = ORIGINAL_KEY + id;
        var transformedFilePrefix = TRANSFORMED_KEY + id;

        var originalFileToUpload = createTempFile(originalFilePrefix, fileSuffix);
        var transformedFileToUpload = createTempFile(transformedFilePrefix, fileSuffix);

        String originalContent = poeticSentence.toLowerCase();
        String transformedContent = poeticSentence.toUpperCase();

        writeContent(originalFileToUpload, originalContent);
        writeContent(transformedFileToUpload, transformedContent);

        var originalFileBucketKey = ORIGINAL_KEY + originalFilePrefix + fileSuffix;
        var transformedFileBucketKey = TRANSFORMED_KEY + transformedFilePrefix + fileSuffix;

        can_upload_file_then_download_file(originalFileToUpload, originalFileBucketKey);
        can_upload_file_then_download_file(transformedFileToUpload, transformedFileBucketKey);

        return new SoratraData(
                can_presign(originalFileBucketKey).toString(),
                can_presign(transformedFileBucketKey).toString()
        );
    }

    private void writeContent(File file, String content) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

    private File can_upload_file_then_download_file(File toUpload, String bucketKey)
            throws IOException {
        bucketComponent.upload(toUpload, bucketKey);

        var downloaded = bucketComponent.download(bucketKey);
        var downloadedContent = Files.readString(downloaded.toPath());
        var uploadedContent = Files.readString(toUpload.toPath());
        if (!uploadedContent.equals(downloadedContent)) {
            throw new RuntimeException("Uploaded and downloaded contents mismatch");
        }

        return downloaded;
    }

    private URL can_presign(String fileBucketKey) {
        return bucketComponent.presign(fileBucketKey, Duration.ofMinutes(2));

    }
}