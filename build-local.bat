@echo off
setlocal
set JAVA_HOME=C:\laragon\bin\jdk17
set ANDROID_HOME=C:\laragon\bin\android-sdk
set ANDROID_SDK_ROOT=C:\laragon\bin\android-sdk
set PATH=%JAVA_HOME%\bin;%PATH%

call gradlew.bat assembleDebug
if errorlevel 1 (
    echo.
    echo Le build a echoue, voir les erreurs ci-dessus.
    pause
    exit /b 1
)

echo.
echo APK genere : app\build\outputs\apk\debug\app-debug.apk
pause
