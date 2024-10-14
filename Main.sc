#!/usr/bin/env -S scala-cli shebang

//> using scala "3.5.1"
//> using dep "dev.zio::zio:2.1.11"
//> using dep "dev.zio::zio-process:0.7.2"

import forsaken.dockerlogreader.App

App.run