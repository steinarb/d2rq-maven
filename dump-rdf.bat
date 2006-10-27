@echo off
set D2R_ROOT=%~p0
set CP=%D2RQ_ROOT%build
call :findjars %D2RQ_ROOT%lib
set LOGCONFIG=file:%D2RQ_ROOT%etc/log4j.properties
java -cp "%CP%" "-Dlog4j.configuration=%LOGCONFIG%" d2rq.dump_rdf %1 %2 %3 %4 %5 %6 %7 %8 %9
exit /B

:findjars
for %%j in (%1\*.jar) do call :addjar %%j
for /D %%d in (%1\*) do call :findjars %%d
exit /B

:addjar
set CP=%CP%;%1
