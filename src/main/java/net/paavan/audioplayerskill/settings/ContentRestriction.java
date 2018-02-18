package net.paavan.audioplayerskill.settings;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;

@Getter
@Builder
public class ContentRestriction {
    @Singular
    private final Map<ContentAbstractionType, String> contentRestrictions;
}
