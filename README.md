# jFairseq: a Java frontend for fairseq Scripted Models

## Motivation

This repo serves to provide limited Java runtime support for scripted [fairseq](https://github.com/pytorch/fairseq/) translation models. 

## Installation

To build this repo, the following repos must first be published to the local Maven repository:

- [sentencepiece-jni](https://github.com/erip/sentencepiece-jni) 
- [jFastBPE](https://github.com/mitre/jfastbpe)

Once this is done, jFairseq can be built by issuing `./gradlew clean build`.

## Tests

Tests can be run by issuing `./gradlew test`.

## Authors

- [Elijah Rippeth](mailto:erippeth@mitre.org)