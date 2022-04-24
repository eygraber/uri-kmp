@echo off
SETLOCAL EnableDelayedExpansion
gradlew :uri:publishMingwX64PublicationToMavenCentralRepository :uri:publishMingwX86PublicationToMavenCentralRepository !*! --no-daemon --no-parallel
