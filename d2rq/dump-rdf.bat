@echo off
set D2R_ROOT=%~p0
set CP="%D2R_ROOT%target\classes"
call :findjars "%D2R_ROOT%lib"
call :findjars "%D2R_ROOT%target\lib"
set LOGCONFIG=log4j.properties
java -cp %CP% -Xmx1G "-Dlog4j.configuration=%LOGCONFIG%" d2rq.dump_rdf %*
exit /B

:findjars
for %%j in (%1\*.jar) do call :addjar "%%j"
for /D %%d in (%1\*) do call :findjars "%%d"
exit /B

:addjar
set CP=%CP%;%1
