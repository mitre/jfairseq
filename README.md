# jFairseq: a Java frontend for fairseq Scripted Models

![Build](https://github.com/mitre/jfairseq/workflows/Build/badge.svg)

## Motivation

This repo serves to provide limited Java runtime support for scripted [fairseq](https://github.com/pytorch/fairseq/) translation models. 

## Installation

To build this repo, the following repos must first be published to the local Maven repository:

- [sentencepiece-jni](https://github.com/erip/sentencepiece-jni)
- [jFastBPE](https://github.com/mitre/jfastbpe)

Once this is done, jFairseq can be built by issuing `sbt clean compile package`.

## Tests

Tests can be run by issuing `sbt  test`.

## Authors

- [Elijah Rippeth](mailto:erippeth@mitre.org)
