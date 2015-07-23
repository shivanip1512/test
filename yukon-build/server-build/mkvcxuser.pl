# Visual Studio creates a <project>.vcxproj.user file to contain user
# specific defaults, like debugging parameters.  This script creates
# a set of these files to match the set of <project>.vcxproj files that
# define applications.

use strict;
use Getopt::Long;
use File::Basename;

my $debug;

GetOptions (
  "d" => \$debug,
)
  or die("Error in command line arguments\n");

recurseDir(".");

sub recurseDir($)
{
  my $dir=shift;

  opendir(DIR, $dir) or die("Unable to open directory $dir");
  my @files = grep { !/^\.{1,2}$/ } readdir (DIR);
  closedir(DIR);

  @files = map {$dir."/".$_} @files;

  foreach my $file (@files)
  {
    if (-d $file)
    {
      recurseDir($file);
    }
    else
    {
      if ($file =~ /.*\.vcxproj$/)
      {
        open(FILE, "<", $file) or die ("Unable to open $file: $!");
        my $appProj=grep(/<ConfigurationType>Application<\/ConfigurationType>/, <FILE>);
        close(FILE);

        if ($appProj)
        {
          # Is there already a .user file?
          my $userFile="$file.user";
          if (! -f $userFile)
          {
            print ("didn't find user file $userFile\n");
            open(NEWUSER, ">", $userFile) or die("Unable to open $userFile: $!");
            print(NEWUSER '<?xml version="1.0" encoding="utf-8"?>
<Project ToolsVersion="12.0" xmlns="http://schemas.microsoft.com/developer/msbuild/2003">
  <PropertyGroup Condition="\'$(Configuration)|$(Platform)\'==\'Debug|Win32\'">
    <LocalDebuggerCommand>$(SolutionDir)bin\$(TargetFilename)</LocalDebuggerCommand>
    <LocalDebuggerWorkingDirectory>$(SolutionDir)</LocalDebuggerWorkingDirectory>
    <DebuggerFlavor>WindowsLocalDebugger</DebuggerFlavor>
  </PropertyGroup>
  <PropertyGroup Condition="\'$(Configuration)|$(Platform)\'==\'Release|Win32\'">
    <LocalDebuggerCommand>$(SolutionDir)bin\$(TargetFilename)</LocalDebuggerCommand>
    <DebuggerFlavor>WindowsLocalDebugger</DebuggerFlavor>
    <LocalDebuggerWorkingDirectory>$(SolutionDir)</LocalDebuggerWorkingDirectory>
  </PropertyGroup>
</Project>');
            close(NEWUSER);
          }
        }
      }
    }
  }
}

