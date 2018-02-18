package net.paavan.audioplayerskill.source;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reads a list of MP3 files from the specified S3 bucket.
 */
@Slf4j
public class S3FileListReader {
    private final AmazonS3 s3Client;
    private final String bucketName;
    private final String prefix;

    public S3FileListReader(final AmazonS3 s3Client, final String bucketName, final String prefix) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.prefix = prefix;
    }

    /**
     * Returns a filtered list of S3 keys for MP3 files read from S3 bucket and prefix.
     *
     * @return List of S3 keys for MP3 files in the specified S3 bucket and prefix
     */
    public List<String> readS3KeysForMp3Files() {
        ListObjectsV2Result result = null;
        List<String> s3KeysForMp3Files = new ArrayList<>();

        do {
            ListObjectsV2Request request = new ListObjectsV2Request();
            request.setBucketName(bucketName);
            request.setPrefix(prefix);
//            request.setMaxKeys(Integer.valueOf(100));
            if (result != null) {
                request.setContinuationToken(result.getNextContinuationToken());
            }

            result = s3Client.listObjectsV2(request);

            List<String> stringList = result.getObjectSummaries().stream()
                    .map(S3ObjectSummary::getKey)
                    .filter(s -> s.endsWith(".mp3"))
                    .collect(Collectors.toList());

            log.info("Adding " + stringList.size() + " files");
            s3KeysForMp3Files.addAll(stringList);

        } while (result.isTruncated());

        log.info("Found keys " + s3KeysForMp3Files.size());
        return s3KeysForMp3Files;
    }
}
