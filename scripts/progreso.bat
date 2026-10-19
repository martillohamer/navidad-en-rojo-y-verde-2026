@echo off
chcp 65001 >nul
echo 🎄 Calculando tu progreso navideno...

call mvn test -Dtest=Dia01Test -q
if %errorlevel% equ 0 (set L1=🟢) else (set L1=🔴)

call mvn test -Dtest=Dia02Test -q
if %errorlevel% equ 0 (set L2=🟢) else (set L2=🔴)

call mvn test -Dtest=Dia03Test -q
if %errorlevel% equ 0 (set L3=🟢) else (set L3=🔴)

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
