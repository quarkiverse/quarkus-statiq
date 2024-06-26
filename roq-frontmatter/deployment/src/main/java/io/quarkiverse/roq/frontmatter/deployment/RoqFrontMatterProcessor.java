package io.quarkiverse.roq.frontmatter.deployment;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import org.jboss.logging.Logger;

import io.quarkiverse.roq.frontmatter.deployment.items.RoqFrontMatterBuildItem;
import io.quarkiverse.roq.frontmatter.runtime.RoqFrontMatterRecorder;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.qute.deployment.TemplatePathBuildItem;
import io.vertx.core.json.JsonObject;

class RoqFrontMatterProcessor {

    private static final Logger LOGGER = org.jboss.logging.Logger.getLogger(RoqFrontMatterProcessor.class);
    private static final String FEATURE = "roq-frontmatter";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void generateTemplate(BuildProducer<TemplatePathBuildItem> templatePathProducer,
            List<RoqFrontMatterBuildItem> roqFrontMatterBuildItems) {
        for (RoqFrontMatterBuildItem item : roqFrontMatterBuildItems) {
            templatePathProducer.produce(TemplatePathBuildItem.builder().path(item.path()).extensionInfo(FEATURE)
                    .content(item.generatedContent()).build());
        }
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void bindFrontMatterData(BuildProducer<SyntheticBeanBuildItem> beansProducer,
            List<RoqFrontMatterBuildItem> roqFrontMatterBuildItems,
            RoqFrontMatterRecorder recorder) {

        for (RoqFrontMatterBuildItem item : roqFrontMatterBuildItems) {
            LOGGER.info("Creating synthetic bean with identifier " + item.path());
            beansProducer.produce(SyntheticBeanBuildItem.configure(JsonObject.class)
                    .scope(ApplicationScoped.class)
                    .setRuntimeInit()
                    .addQualifier().annotation(Named.class).addValue("value", item.path()).done()
                    .runtimeValue(recorder.createRoqDataJson(item.fm()))
                    .done());
        }
    }

}
