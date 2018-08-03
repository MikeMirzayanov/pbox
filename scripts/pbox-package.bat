if "%2"=="" (
    pbox package %1 --templateDir=N:\pbox\templates --packageDir=N:\pbox\repo.pbox.me\1.0
) else (
    pbox package %1 --templateDir=N:\pbox\templates --packageDir=N:\pbox\repo.pbox.me\1.0 --version=%2
)