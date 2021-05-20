package com.afoix.metadatavalidator.loaders;

import com.afoix.metadatavalidator.entities.Entity;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.stream.Stream;

public interface EntityLoader<EntityType extends Entity> {
    Stream<EntityType> load() throws IOException, InvalidFormatException;
}
