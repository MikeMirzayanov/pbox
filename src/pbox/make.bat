@echo off

IF "%1"=="" (
    echo Usage: make ^<profile-name^>
    echo Example: make dev
) ELSE (
    IF NOT EXIST profiles\%1.properties (
        echo ERROR: file "profiles\profiles.%1.xml" not found
        pause
    ) ELSE (
        call fm --properties=profiles/%1.properties pom.xml.ftl > pom.xml
        if errorlevel 1 pause
        call mvn clean package -Dmaven.test.skip=true -Dfile.encoding=UTF-8
    )
)
