package net.paavan.audioplayerskill.source;

import lombok.extern.slf4j.Slf4j;
import net.paavan.audioplayerskill.settings.ContentAbstractionType;
import net.paavan.audioplayerskill.settings.PlaybackSettings;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class TheDewaristsMusicSource implements MusicSource {
    private final String ALBUM_NAME = "The Dewarists";
    private final String S3_ALBUM_PATH = "The Dewarists";
    private final String S3_HTTPS_PREFIX = "https://s3.amazonaws.com/audioplayerskill/";

    private List<PlayableEntity> playableEntities;

    @Override
    public void populateSourceFromS3Keys(final List<String> s3Keys) {
        playableEntities = s3Keys.stream()
                .map(this::convertS3KeyToPlayableEntity)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        log.info("The Dewarists source populated with " + playableEntities.size() + " playable entities");
    }

    @Override
    public boolean canHandle(final PlaybackSettings playbackSettings) {
        if (playbackSettings.getContentRestriction().getContentRestrictions()
                .containsKey((ContentAbstractionType.ALBUM))) {
            String albumName = playbackSettings.getContentRestriction().getContentRestrictions()
                    .get(ContentAbstractionType.ALBUM);
            return albumName.equals(ALBUM_NAME);
        }

        return true;
    }

    @Override
    public List<String> getAudioUrls() {
        return playableEntities.stream()
                .map(PlayableEntity::getS3Key)
                .map(s3Key -> S3_HTTPS_PREFIX + s3Key)
                .collect(Collectors.toList());
    }

    private Optional<PlayableEntity> convertS3KeyToPlayableEntity(final String s3Key) {
        PlayableEntity.PlayableEntityBuilder playableEntityBuilder = PlayableEntity.builder()
                .s3Key(s3Key);

        List<String> tokens = Arrays.asList(s3Key.split("/"));

        // This assumes that The Dewarists keys are structured as follows:
        // "Songs/<AlbumName>/<SeasonName>/<SongName>"
        // All other file structures will be ignored.
        if (tokens.size() == 4 && tokens.get(1).equals(S3_ALBUM_PATH) && tokens.get(3).endsWith(".mp3")) {
            playableEntityBuilder.entityDescription(ContentAbstractionType.ALBUM, ALBUM_NAME);
            playableEntityBuilder.entityDescription(ContentAbstractionType.SEASON, tokens.get(2));
            playableEntityBuilder.entityDescription(ContentAbstractionType.SONG, tokens.get(3));
            return Optional.of(playableEntityBuilder.build());
        }

        if (s3Key.contains(S3_ALBUM_PATH)) {
            log.error("Unable to convert S3 Key to Playable Entity: " + s3Key);
        }
        return Optional.empty();
    }
}
