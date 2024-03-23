@echo off
setlocal enabledelayedexpansion

set PBOX_VERSION=1.0
set PBOX_RND=%RANDOM%

set OFFLINE=0
for %%p in (%*) do (
    if "%%~p"=="-o" (
        set OFFLINE=1
    )
)

call "%~dp0bin\test-sudo.bat"

if errorlevel 1 (
    "%~dp0bin\setcolor" ri
    echo Please run PBOX in administrative console: try Shift+RightClick and 'Run as administrator'. Or run 'pbox -uac' to disable UAC ^(reboot required^).
    "%~dp0bin\setcolor" rgb
    pause
    exit 1
)

echo Locating java.exe...
set PBOX_HOME=%~dp0&&set PBOX_HOME=!PBOX_HOME:~0,-1!
setx PBOX_HOME "!PBOX_HOME!" /M > nul
"!PBOX_HOME!\bin\pathed.exe" -a "!PBOX_HOME!" -s 2> nul

:locateJavaExe
for %%j in ("java.exe" "%ALLUSERSPROFILE%\pbox\jre\jre\bin\java.exe" "!PBOX_HOME!\jre\jre\bin\java.exe" "%ProgramFiles(x86)%\Java\jre7\bin\java.exe" "%ProgramFiles%\Java\jre7\bin\java.exe") do (
    %%j -version 1>%TEMP%\java.pbox.out 2>%TEMP%\java.pbox.err
    if not errorlevel 1 (
        "!PBOX_HOME!\bin\contains.exe" "%TEMP%\java.pbox.err" "Java" > nul
        if errorlevel 1 (
            "!PBOX_HOME!\bin\contains.exe" "%TEMP%\java.pbox.err" "1.7" > nul
            if errorlevel 1 (
                set PBOX_JAVA_EXE=%%j
                echo Found Java 7: !PBOX_JAVA_EXE!
                goto foundJavaExe
            )
            "!PBOX_HOME!\bin\contains.exe" "%TEMP%\java.pbox.err" "1.8" > nul
            if errorlevel 1 (
                set PBOX_JAVA_EXE=%%j
                echo Found Java 8: !PBOX_JAVA_EXE!
                goto foundJavaExe
            )
            "!PBOX_HOME!\bin\contains.exe" "%TEMP%\java.pbox.err" "build 9" > nul
            if errorlevel 1 (
                set PBOX_JAVA_EXE=%%j
                echo Found Java 9: !PBOX_JAVA_EXE!
                goto foundJavaExe
            )
            "!PBOX_HOME!\bin\contains.exe" "%TEMP%\java.pbox.err" "build 10" > nul
            if errorlevel 1 (
                set PBOX_JAVA_EXE=%%j
                echo Found Java 10: !PBOX_JAVA_EXE!
                goto foundJavaExe
            )
            "!PBOX_HOME!\bin\contains.exe" "%TEMP%\java.pbox.err" "build 11" > nul
            if errorlevel 1 (
                set PBOX_JAVA_EXE=%%j
                echo Found Java 11: !PBOX_JAVA_EXE!
                goto foundJavaExe
            )
            "!PBOX_HOME!\bin\contains.exe" "%TEMP%\java.pbox.err" "build 12" > nul
            if errorlevel 1 (
                set PBOX_JAVA_EXE=%%j
                echo Found Java 12: !PBOX_JAVA_EXE!
                goto foundJavaExe
            )
            "!PBOX_HOME!\bin\contains.exe" "%TEMP%\java.pbox.err" "build 13" > nul
            if errorlevel 1 (
                set PBOX_JAVA_EXE=%%j
                echo Found Java 13: !PBOX_JAVA_EXE!
                goto foundJavaExe
            )
            "!PBOX_HOME!\bin\contains.exe" "%TEMP%\java.pbox.err" "build 14" > nul
            if errorlevel 1 (
                set PBOX_JAVA_EXE=%%j
                echo Found Java 14: !PBOX_JAVA_EXE!
                goto foundJavaExe
            )
            "!PBOX_HOME!\bin\contains.exe" "%TEMP%\java.pbox.err" "build 15" > nul
            if errorlevel 1 (
                set PBOX_JAVA_EXE=%%j
                echo Found Java 15: !PBOX_JAVA_EXE!
                goto foundJavaExe
            )
            "!PBOX_HOME!\bin\contains.exe" "%TEMP%\java.pbox.err" "build 17" > nul
            if errorlevel 1 (
                set PBOX_JAVA_EXE=%%j
                echo Found Java 17: !PBOX_JAVA_EXE!
                goto foundJavaExe
            )
        )
    )
)

if "!PBOX_JAVA_EXE!"=="" (
    "!PBOX_HOME!\bin\setcolor" rgi
    echo Java has not been found. Downloading...
    "!PBOX_HOME!\bin\setcolor" rgb
    "!PBOX_HOME!\bin\wget.exe" "--output-document=%TEMP%\pbox.jre.7z" http://repo.pbox.me/files/jre.7z
    if not errorlevel 1 (
        if exist "%TEMP%\pbox.jre.7z" (
            echo JRE has been downloaded
            echo Extracting...
            md "!PBOX_HOME!\jre"
            "!PBOX_HOME!\bin\7za.exe" x -y "-o!PBOX_HOME!\jre" "%TEMP%\pbox.jre.7z" > nul
            echo Done. Exctracted JRE. Trying to locate java.exe again...
            goto locateJavaExe
        )
    )
)

:foundJavaExe

echo.

if not exist "!PBOX_HOME!\pbox-%PBOX_VERSION%.jar" (
    echo pbox-%PBOX_VERSION%.jar has not been found. Downloading...
    goto downloadPboxJar
)

if "%OFFLINE%"=="0" (
    "!PBOX_HOME!\bin\wget.exe" "--output-document=%TEMP%\pbox-%PBOX_VERSION%.jar.md5" http://repo.pbox.me/files/pbox-%PBOX_VERSION%.jar.md5 1>nul 2>nul
    fc "%TEMP%\pbox-%PBOX_VERSION%.jar.md5" "!PBOX_HOME!\pbox-%PBOX_VERSION%.jar.md5" 1>nul 2>nul

    if not errorlevel 1 (
        goto hasPboxJar
    )
) else (
    goto hasPboxJar
)

:downloadPboxJar
"!PBOX_HOME!\bin\setcolor" rgi
echo PBOX is abscent or not the last release. Downloading...
"!PBOX_HOME!\bin\setcolor" rgb
"!PBOX_HOME!\bin\wget.exe" "--output-document=!PBOX_HOME!\pbox-%PBOX_VERSION%.jar" http://repo.pbox.me/files/pbox-%PBOX_VERSION%.jar
"!PBOX_HOME!\bin\wget.exe" "--output-document=!PBOX_HOME!\pbox-%PBOX_VERSION%.jar.md5" http://repo.pbox.me/files/pbox-%PBOX_VERSION%.jar.md5 1>nul 2>nul
echo PBOX has been updated!
echo.

:hasPboxJar

@md "!PBOX_HOME!\temp" 1>nul 2>nul
pushd "!PBOX_HOME!\temp"
!PBOX_JAVA_EXE! -Dfile.encoding=UTF-8 -cp .;"!PBOX_HOME!\pbox-%PBOX_VERSION%.jar" me.pbox.Main %*
popd
