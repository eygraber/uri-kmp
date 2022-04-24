@echo off
SETLOCAL EnableDelayedExpansion
gradlew :uri:clean :uri:build :uri:publishMingwX64PublicationToMavenCentralRepository :uri:publishMingwX86PublicationToMavenCentralRepository !*! --no-daemon --no-parallel
