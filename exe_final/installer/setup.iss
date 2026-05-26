; PC Remote Listener — Inno Setup Installer Script
; Author: Sebai Mohamed Safa

#define MyAppName "PC Remote Listener"
#define MyAppVersion "1.0"
#define MyAppPublisher "Sebai Mohamed Safa"
#define MyAppURL "https://github.com/smosaker/remote-control"
#define MyAppExeName "PCRemoteListener.exe"

[Setup]
AppId={{B8F4A32C-9E2D-4A1B-9F3C-7D5E8A2B4C6D}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppName}
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
LicenseFile=eula.txt
InfoBeforeFile=readme.txt
OutputDir=.
OutputBaseFilename=PCRemoteListener_Setup
Compression=lzma2/max
SolidCompression=yes
WizardStyle=modern
PrivilegesRequired=admin
SetupIconFile=..\PCRemoteListener\server.ico
UninstallDisplayIcon={app}\{#MyAppExeName}
DisableProgramGroupPage=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "Create a &desktop shortcut"; GroupDescription: "Additional icons:"; Flags: checkedonce

[Files]
Source: "..\PCRemoteListener\bin\Release\net8.0-windows\win-x64\publish\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\PCRemoteListener\server.ico"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; WorkingDir: "{app}"; IconFilename: "{app}\server.ico"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"; IconFilename: "{app}\server.ico"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; WorkingDir: "{app}"; IconFilename: "{app}\server.ico"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "Launch {#MyAppName} now"; Flags: nowait postinstall skipifsilent unchecked

[UninstallRun]
Filename: "taskkill"; Parameters: "/f /im {#MyAppExeName}"; Flags: runhidden

[Code]
procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssPostInstall then
  begin
    if FileExists(ExpandConstant('{app}\{#MyAppExeName}')) then
      Log('Installation completed successfully');
  end;
end;
