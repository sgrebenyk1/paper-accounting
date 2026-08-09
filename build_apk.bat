@echo off
echo ==========================================
echo   Building Android APK (Paper Inventory)  
echo ==========================================

if exist "gradlew.bat" (
    set GRADLE_CMD=gradlew.bat
) else (
    set GRADLE_CMD=gradle
)

echo Running assembleRelease assembleDebug...
call %GRADLE_CMD% assembleRelease assembleDebug

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Build failed. Please check errors above.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ==========================================
echo   BUILD SUCCESSFUL!
echo ==========================================
echo APKs generated at:
echo   Release APK: app\build\outputs\apk\release\app-release.apk
echo   Debug APK:   app\build\outputs\apk\debug\app-debug.apk
echo ==========================================
pause
