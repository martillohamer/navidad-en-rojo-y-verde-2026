@echo off
chcp 65001 >nul
echo 🎄 Calculando tu progreso navideno...

call mvn test -Dmaven.test.failure.ignore=true -q >nul 2>&1

set L1=🔴
set L2=🔴
set L3=🔴

if exist target\surefire-reports\reto.dia01.Dia01Test.txt (
    findstr /C:"FAILURE!" target\surefire-reports\reto.dia01.Dia01Test.txt >nul
    if errorlevel 1 set L1=🟢
)
if exist target\surefire-reports\reto.dia02.Dia02Test.txt (
    findstr /C:"FAILURE!" target\surefire-reports\reto.dia02.Dia02Test.txt >nul
    if errorlevel 1 set L2=🟢
)
if exist target\surefire-reports\reto.dia03.Dia03Test.txt (
    findstr /C:"FAILURE!" target\surefire-reports\reto.dia03.Dia03Test.txt >nul
    if errorlevel 1 set L3=🟢
)

echo.
echo         *        
echo        /_\       
echo       /%L1 %L2_\      
echo      /___%L3___\     
echo     / %L1   %L2 \    
echo    /___________\     
echo         ||       
echo         ||       
echo.
echo Día 1: %L1% | Día 2: %L2% | Día 3: %L3%
