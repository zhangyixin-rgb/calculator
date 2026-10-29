@echo off
setlocal

set "MAVEN_VERSION=3.9.16"
set "DIST_DIR=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%"
set "MVN_CMD=%DIST_DIR%\apache-maven-%MAVEN_VERSION%\bin\mvn.cmd"

if not exist "%MVN_CMD%" (
    if not exist "%DIST_DIR%" mkdir "%DIST_DIR%"
    set "DIST_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"
    echo Downloading Apache Maven %MAVEN_VERSION% ...
    powershell -NoProfile -Command "$ErrorActionPreference='Stop'; Invoke-WebRequest -Uri $env:DIST_URL -OutFile (Join-Path $env:DIST_DIR 'maven.zip') -UseBasicParsing; Expand-Archive -Path (Join-Path $env:DIST_DIR 'maven.zip') -DestinationPath $env:DIST_DIR -Force"
    if errorlevel 1 (
        echo ERROR: failed to download or extract Apache Maven.
        exit /b 1
    )
    del /q "%DIST_DIR%\maven.zip" 2>nul
)

call "%MVN_CMD%" %*
exit /b %ERRORLEVEL%
