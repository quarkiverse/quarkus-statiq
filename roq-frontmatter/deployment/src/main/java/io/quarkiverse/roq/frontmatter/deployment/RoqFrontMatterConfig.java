package io.quarkiverse.roq.frontmatter.deployment;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "roq.frontmatter")
public class RoqFrontMatterConfig {

    private final static String DEFAULT_LOCATION = "posts";

    /**
     * The location of the Roq data files.
     */
    @ConfigItem(defaultValue = DEFAULT_LOCATION)
    public String location;
}
