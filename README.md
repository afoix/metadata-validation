# Metadata-validation

This repository contains the metadata-validation framework developed as part of my thesis.

## Repository layout

* `/core-library`: Contains the validator framework itself, including tests.

There are two applications built on top of the framework for demonstration purposes:

* `/faang-validation`: Contains a command-line tool built on the framework which implements the checklist-style validation used by FAANG and BioSamples. Support for building a Docker image is included.
* `/webservice`: Contains a Spring MVC web service which validates XML metadata against the ENA XML schemas, and also does validation of NCBI Taxon identifiers to make sure they are actually existing in the ontology.

Also there is:

* `/comparison`: Contains things for building a Docker image from the FAANG metadata validation tool for use in comparisons to this project.

