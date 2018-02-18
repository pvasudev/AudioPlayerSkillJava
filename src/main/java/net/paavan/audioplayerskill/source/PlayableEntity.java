package net.paavan.audioplayerskill.source;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import net.paavan.audioplayerskill.settings.ContentAbstractionType;

import java.util.Map;

@Getter
@Builder
@ToString
public class PlayableEntity {
    private final String s3Key;
    @Singular("entityDescription")
    private final Map<ContentAbstractionType, String> entityDescription;
}
