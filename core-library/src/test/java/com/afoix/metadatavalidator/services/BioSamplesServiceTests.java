package com.afoix.metadatavalidator.services;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BioSamplesServiceTests {

    private final BioSamplesService service = new BioSamplesService();

    @Test
    public void fetchSample_withValidSampleId_returnsSampleEntity() throws IOException, InvalidAttributeNameOrPathException {
        Entity entity = service.fetchSample("SAMEA676028");
        assertThat(entity).isNotNull();
        assertThat(entity.hasAttribute("accession")).isTrue();
        assertThat(entity.getAttributeValues("accession")).singleElement().isEqualTo("SAMEA676028");
    }

    @Test
    public void fetchSample_withInvalidSampleId_throwsFileNotFoundException() {
        assertThatThrownBy(() -> service.fetchSample("INVALID123")).isInstanceOf(FileNotFoundException.class);
    }
}
