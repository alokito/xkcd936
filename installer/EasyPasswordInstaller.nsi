;NSIS Modern User Interface version 1.70
;Easy Password Installer Script
;Written by Alok Saldanha
;Adaped from SimpleText Installer Script
;Written by Stephen Strenn

;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

;--------------------------------
;General

  ;Name and file
  Name "EasyPassword"
  OutFile "EasyPasswordInstaller.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\xkcd936\EasyPassword"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\EasyPassword" ""

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING
	!define MUI_HEADERIMAGE "C:\temp\xkcd936\static\images\xkcd936.bmp"
	!define MUI_HEADERIMAGE_BITMAP_NOSTRETCH
	!define MUI_HEADERIMAGE_BITMAP "C:\temp\xkcd936\static\images\xkcd936.bmp"
	!define MUI_ICON "C:\temp\xkcd936\static\images\setup.ico"
	!define MUI_UNICON "C:\temp\xkcd936\static\images\setup.ico"

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "EasyPassword (required)" SecDummy

  SectionIn RO

  ;Files to be installed
  SetOutPath "$INSTDIR"
  
   File "C:\temp\xkcd936\dist\xkcd936swing.jar"
   File "C:\temp\xkcd936\static\images\xkcd936.ico"

    ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\EasyPassword "Install_Dir" "$INSTDIR"
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\EasyPassword" "DisplayName" "EasyPassword"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\EasyPassword" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\EasyPassword" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\EasyPassword" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
SectionEnd

; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts"
  CreateDirectory "$SMPROGRAMS\EasyPassword"
  CreateShortCut "$SMPROGRAMS\EasyPassword\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe"
  CreateShortCut "$SMPROGRAMS\EasyPassword\EasyPassword.lnk" "$INSTDIR\xkcd936swing.jar" "" "$INSTDIR\xkcd936.ico"
SectionEnd

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\EasyPassword"
  DeleteRegKey HKLM SOFTWARE\EasyPassword
  DeleteRegKey /ifempty HKCU "Software\EasyPassword"

	; Remove shortcuts
  RMDir /r "$SMPROGRAMS\EasyPassword"

  ; Remove directories used
  RMDir /r "$INSTDIR"

SectionEnd