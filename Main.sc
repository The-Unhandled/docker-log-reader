#!/usr/bin/env -S scala-cli shebang

//> using scala "3.5.1"
//> using dep "dev.zio::zio:2.1.11"
//> using dep "dev.zio::zio-process:0.7.2"
//> using dep "org.typelevel::cats-core:2.12.0"
//> using test.dep "dev.zio::zio-test:2.1.11"
//> using test.dep "dev.zio::zio-test-sbt:2.1.11"
//> using test.resourceDir src/test/resources

import forsaken.dockerlogreader.App

App.run