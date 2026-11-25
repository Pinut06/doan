@echo off
setlocal
set "PEANUT_FRONTEND=%~dp0."
set "PEANUT_FRONTEND=%PEANUT_FRONTEND:\=/%"
set "PEANUT_RUNTIME=%TEMP:\=/%"
if not defined XAMPP_HOME set "XAMPP_HOME=D:\xampp"
if not exist "%XAMPP_HOME%\apache\bin\httpd.exe" (
    echo Set XAMPP_HOME to your XAMPP installation directory.
    exit /b 1
)
echo Frontend: http://127.0.0.1:3000/    Backend: http://127.0.0.1:8082/
"%XAMPP_HOME%\apache\bin\httpd.exe" -d "%XAMPP_HOME%\apache" -f "%~dp0dev-httpd.conf" -X
exit /b %errorlevel%
