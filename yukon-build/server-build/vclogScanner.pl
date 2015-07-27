use strict;
use Getopt::Long;
use Text::ParseWords;
use File::Basename;
use File::Spec;
use Data::Dumper;

my $debug;
my $generateMPC;

GetOptions (
  "d" => \$debug,
  "m=s" => \$generateMPC,
)
  or die("Error in command line arguments\n");


my %includesByObject;
my %definesByObject;
my %optionsByObject;
my $dir=".";

if ($generateMPC)
{
  open(MWC, ">", "$generateMPC.mwc") or die ("Unable to open $generateMPC.mwc file: $!");
  print(MWC "workspace($generateMPC) {\n");
}

# scan through stdin
while(<>)
{
  if (/^[a-zA-Z\\]*cl .*[-\/]Fe/ or /^[a-zA-Z\\]*cl.exe .*[-\/]Fe/ )
  {
    # Match cl command for link
    doLink($_);
  } 
  elsif (/^[a-zA-Z\\]*cl / or /^[a-zA-Z\\]*cl.exe/ )
  {
    # Match cl command for compile
    doCl($_);
  } 
  elsif (/^[a-zA-Z\\]*rc / or /^[a-zA-Z\\]*rc.exe /)
  {
    # Match rc command
    doRc($_);
  }
  elsif (/^[a-zA-Z\\]*lib / or /^[a-zA-Z\\]*lib.exe /)
  {
    # Match lib command
    doLib($_);
  }
  elsif (/^Building in .*\\yukon-server\\(.*)/)
  {
    # note what directory we're in.
    $dir=$1;
    print ("in directory $dir\n") if ($debug);
  }
}

if ($generateMPC)
{
  print(MWC "}\n");
  close(MWC);
}

# We've detcted an cl command, parse it
sub doCl($)
{
  my @defines;
  my @includes;
  my %options;
  my @objects;
  my $object;
  my $fo;

  my $line=shift;
  chomp($line);
  $line =~ s/\\/\//g;
  print ("CL: $line\n") if ($debug);
  my @parms = quotewords('\s+', 1, $line);
  shift @parms; # Get rid if the cl
  my $parm;
  while (defined($parm = shift @parms))
  {
    print("parm: $parm\n") if ($debug);
    if ($parm =~ /^[-\/]D(.*)/)
    {
      #Define
      print("Define: $1\n") if ($debug);
      push(@defines, $1);
    } 
    elsif ($parm =~ /^[-\/]I(.*)/)
    {
      #Include
      my $include=parseOrShift($1, \@parms);
      print("Include: $include\n") if ($debug);
      push(@includes, resolvePath($include));
    }
    elsif ($parm =~ /^[-\/]FI(.*)/)
    {
      #Force include
      my $fi=parseOrShift($1, \@parms);
      print("FI $fi\n") if ($debug);
      $options{"/FI"}=$fi;
    }
    elsif ($parm =~ /^[-\/]c/)
    {
      print("c Option\n") if ($debug);
    }
    elsif ($parm =~ /^[-\/]Fp(.*)/)
    {
      #Precompiled Header option
      my $pch=parseOrShift($1, \@parms);
      $pch=resolvePath($pch);
      print("PCH Option: /Fp$pch\n") if ($debug);
      $options{"/Fp"}=$pch;
    }
    elsif ($parm =~ /^[-\/]Fo(.*)/)
    {
      #Object Output option
      $fo=parseOrShift($1, \@parms);
      # The build lies to us, compiles in Message\Serialization\Thrift are reported as going to ., but they are really there
      if ($fo ne "./")
      {
        $fo=resolvePath($fo);
      }
      else
      {
        $fo="";
        $dir="Message/Serialization/Thrift";
      }
      print("Fo Option: /Fo$fo\n") if ($debug);
      $options{"/Fo"}=$fo;
    }
    elsif ($parm =~ /^[-\/]([a-zA-Z]*)(.*)/)
    {
      #Other option
      print("Option: /$1$2\n") if ($debug);
      $options{"/".$1}=$2;
    }
    elsif ($parm =~ /^(.*\.cpp)$/i)
    {
      #c/c++ object
      my $source;
      print("dir=$dir, fo=$fo, 1=$1\n") if ($debug);
      $object=File::Spec->catdir(($dir, $fo, basename($1,".cpp").".obj"));
      $source="$1";
      if (File::Spec->file_name_is_absolute($source))
      {
        $source=File::Spec->abs2rel($source, $dir);
      }
      print("object $object from $source\n") if ($debug);
      addSource($object, $source);
      push(@objects, $object);
    }
    elsif ($parm =~ /^(.*\.c)$/i)
    {
      #c/c++ object
      my $source;
      print("dir=$dir, fo=$fo, 1=$1\n") if ($debug);
      $object=File::Spec->catdir(($dir, $fo, basename($1,".c").".obj"));
      $source="$1";
      if (File::Spec->file_name_is_absolute($source))
      {
        $source=File::Spec->abs2rel($source, $dir);
      }
      print("object $object from $source\n") if ($debug);
      addSource($object, $source);
      push(@objects, $object);
    }
    else
    {
      print "Invalid parm $parm\n";
    }
  }

  foreach $object (@objects)
  {
    if ($debug)
    {
      print("object: $object\n");
      print("  defines: ".join(" ", @defines)."\n");
      print("  includes: ".join(" ", @includes)."\n");
      print("  options: ");
      print("$_ $options{%_} ") for (keys %options);
      print("\n");
    }

    $definesByObject{$object}=\@defines;
    $includesByObject{$object}=\@includes;
    $optionsByObject{$object}=\%options;
  }
}

# We've detcted an rc command, parse it
sub doRc($)
{
  my @defines;
  my @objects;
  my $output;
  my $source;

  my $line=shift;
  chomp($line);
  $line =~ s/\\/\//g;
  print ("RC: $line\n") if ($debug);
  my @parms = quotewords('\s+', 1, $line);
  shift @parms; # Get rid if the cl
  my $parm;
  while (defined($parm = shift @parms))
  {
    print("parm: $parm\n") if ($debug);
    if ($parm =~ /^[-\/][Dd](.*)/)
    {
      #Define
      print("Define: $1\n") if ($debug);
      push(@defines, $1);
    } 
    elsif ($parm =~ /^[-\/][Ff][Oo](.*)/)
    {
      #Output
      $output=resolvePath(parseOrShift($1, \@parms));
      $output =~ s/\.\/obj\///;
      push(@objects, $output);
      print("Output: $output\n") if ($debug);
    } 
    elsif ($parm =~ /^[-\/][Rr]/)
    {
      # -R Ignored
    }
    elsif ($parm =~ /^(.*\.rc)/)
    {
      # .rc object
      print("res: $1\n") if ($debug);
      $source=resolvePath($1);
    }
    else
    {
      print "Invalid parm $parm\n";
    }
  }
  print "source=$source, output=$output\n";
  addSource($output, $source);

  foreach my $object (@objects)
  {
    $definesByObject{$object}=\@defines;
  }
}

# We've detcted an lib command, parse it
sub doLib($)
{
  my @objects;
  my @libSources;
  my $library;

  $dir="Message";
  my $line=shift;
  $line =~ s/\\/\//g;
  print ("LIB: $line\n") if ($debug);
  my @parms=split(" ", $line);
  @parms = quotewords('\s+', 1, $line);
  my $parm;
  shift @parms; # Get rid if the cl
  while (defined($parm = shift @parms))
  {
    if ($parm =~ /^[-\/]OUT:\"*([^"]*)\"*/)
    {
      print("Output: $1\n") if ($debug);
      $library=$1;
    }
    elsif ($parm =~ /(.*\.obj)/)
    {
      my $object=File::Spec->catdir(("Message\\Serialization\\Thrift", $1));
      print("Input: $object from ".getSource($object)."\n") if ($debug);
      push(@objects, $object);
      push(@libSources, "Serialization\\Thrift\\".getSource($object));
    }
    else
    {
      print "Invalid parm $parm\n";
    }
  }

  print "\nLibrary $library\n";
  print " Sources: ".join(" ", @libSources)."\n";
#  print " Defines: ".join(" ", findCommon(\%definesByObject, \@objects))."\n";
#  print " Includes: ".join(" ", findCommon(\%includesByObject, \@objects))."\n";

  print " Objects:\n";
  my %objDefines;
  my %objIncludes;
  my %objOptions;
  my %options;

  foreach my $object (@objects)
  {
    if (defined($definesByObject{$object}))
    {
      my @defines=@{$definesByObject{$object}};
#      @defines=removeCommon(\@commonDefines, \@defines);
      if (@defines > 0)
      {
        print "    Defines: ".join(";", @defines)."\n";
        %objDefines=(%objDefines, map{$_=>1} @defines);
      }
    }

    if (defined($includesByObject{$object}))
    {
      my @includes=@{$includesByObject{$object}};
#      @includes=removeCommon(\@commonIncludes, \@includes);
      if (@includes > 0)
      {
        print "    Includes: ".join(";", @includes)."\n";
        %objIncludes=(%objIncludes, map{$_=>1} @includes);
      }
    }

    if (defined($optionsByObject{$object}))
    {
      %objOptions=%{$optionsByObject{$object}};
#      @options=removeCommon(\@commonOptions, \@options);
      if (%objOptions > 0)
      {
        print "    Options: ".join(";", keys %objOptions)."\n";
        %options=(%options, %objOptions);
      }
    }
  }

  print(" Composite Defines: ".join(" ", keys %objDefines)."\n");
  print(" Composite Includes: ".join(" ", keys %objIncludes)."\n");

  if ($generateMPC)
  {
    $library =~ /([^.]*)\..*/;
    print("library=$library\n");
    my $basename = basename($library, (".lib"));
    print("library=$library, basename=$basename\n");

    open(MPC, ">", "$dir/$basename.mpc") or die ("Unable to open $dir/$basename.mpc file: $!");
    print(MPC "project($basename) {\n");
    print(MPC "  staticname=$basename\n");

    foreach my $define (sort keys %objDefines)
    {
      $define =~ s/ /\\ /g;
      $define =~ s/"//g;
      # Make sure that DEBUG is only in the Debug configurarion
      if ($define =~ /^_DEBUG$/)
      {
        print(MPC "  specific {\n");
        print(MPC "    Debug::macros += $define\n");
        print(MPC "  }\n");
      }
      else
      {
        print(MPC "  macros += $define\n");
      }
    }

    foreach my $include (sort keys %objIncludes)
    {
      print(MPC "  includes += $include\n");
    }

    if (defined($objOptions{"/Fp"}))
    {
      print(MPC "  pch_header = precompiled.h\n");
      print(MPC "  pch_source = ../precompiled.cpp\n\n");
    }

    print(MPC "  Source_Files {\n");
    if (defined($objOptions{"/Fp"}))
    {
      print(MPC "    ../precompiled.cpp\n");
    }
    foreach my $source (@libSources)
    {
      print(MPC "    $source\n");
    }
    print(MPC "  }\n\n");
    print(MPC "  specific {\n");
    print(MPC "    postbuild= \\ns {\n");
    print(MPC "xcopy /d/y \$(OutDir)\$(TargetFileName) \$(SolutionDir)bin\n");
    print(MPC "if exist \$(OutDir)\$(TargetName).lib xcopy /d/y \$(OutDir)\$(TargetName).lib \$(SolutionDir)lib\n");
    print(MPC "if exist \$(OutDir)\$(TargetName).lib xcopy /d/y \$(OutDir)\$(TargetName).lib \$(SolutionDir)bin\n");
    print(MPC "    }\n");
    print(MPC "  }\n\n");
    print(MPC "  specific {\n");
    print(MPC "    Release::lib_modifier =\n");
    print(MPC "    Debug::lib_modifier =\n");
    print(MPC "    Release::runtime_library=MultiThreadedDLL\n");
    print(MPC "    Debug::runtime_library=MultiThreadedDebugDLL\n");
    print(MPC "  }\n\n");
    print(MPC "}\n\n");

    close(MPC);

    print(MWC "        static {\n");
    print(MWC "          cmdline += -static\n");
    print(MWC "          $dir/$basename.mpc\n");
    print(MWC "        }\n");
  }
}

# We've detcted an link command, parse it
sub doLink($)
{
  my @objects;
  my @options;
  my @libs;
  my @resources;
  my @libSources;
  my $exec;
  my %resDefines;
  my @afters;

  my $line=shift;
  $line =~ s/\\/\//g;
  print ("LINK: $line\n") if ($debug);
  my @parms=split(" ", $line);
  @parms = quotewords('\s+', 1, $line);
  shift @parms; # Get rid if the cl
  my $parm;
  while (defined($parm = shift @parms))
  {
    print("parm: $parm\n") if ($debug);
    if ($parm =~ /^[-\/]Fe(.*)/)
    {
      print("Executable: $1\n") if ($debug);
      $exec=$1;
    }
    elsif ($parm =~ /^[-\/]D(.*)/)
    {
      #Define
      print("Define: $1\n") if ($debug);
    } 
    elsif ($parm =~ /^[-\/]I(.*)/)
    {
      #Include
      my $include=resolvePath(parseOrShift($1, \@parms));
      print("Include: $include\n") if ($debug);
    }
    elsif ($parm =~ /^[-\/](.*)/)
    {
      #Other option
      print("Option: $1\n") if ($debug);
      push(@options, $1);
    }
    elsif ($parm =~ /^(.*\.obj)/)
    {
      #object
      print("dir=$dir, fo=./obj, 1=$1\n") if ($debug);
      my $object;
      $object=$1;
      if (File::Spec->file_name_is_absolute($object))
      {
        $object=File::Spec->abs2rel($object, $dir);
      }
      elsif(not ($1 =~ /^\.\/obj/))
      {
        $object=File::Spec->catdir(($dir, "./obj", $object));
      }
      else
      {
        $object=File::Spec->catdir(($dir, $object));
      }
      $object=resolvePath($object);
      print("object $object\n") if ($debug);
      push(@objects, $object);
    }
    elsif ($parm =~ /^(.*\.res)/)
    {
      #object
      print("compiled resource $1\n") if ($debug);
      push(@resources, $1);
    }
    elsif ($parm =~ /^(.*\.lib)/)
    {
      #object
      print("library $1\n") if ($debug);
      my $lib=resolvePath($1);
      push(@libs, $lib);
    }
    else
    {
      print "Invalid parm $parm\n";
    }
  }

  my $isExe;
  if ($exec =~ /.*\.exe$/)
  {
    print "\nExecutable $exec\n";
    $isExe=1;
  }
  else
  {
    print "\nDLL $exec\n";
  }

  print " Objects:\n";
  my %objDefines;
  my %objIncludes;
  my %objOptions;
  my %options;  # Aggregate options
  foreach my $object (@objects)
  {
    print("$object: ".getSource($object)."\n");
    if (!defined(getSource($object)))
    {
      print("Unable to find source for $object\n");
    }

    if (defined($definesByObject{$object}))
    {
      my @defines=@{$definesByObject{$object}};
#      @defines=removeCommon(\@commonDefines, \@defines);
      if (@defines > 0)
      {
        print "    Defines: ".join(";", @defines)."\n";
        %objDefines=(%objDefines, map{$_=>1} @defines);
      }
    }

    if (defined($includesByObject{$object}))
    {
      my @includes=@{$includesByObject{$object}};
#      @includes=removeCommon(\@commonIncludes, \@includes);
      if (@includes > 0)
      {
        print "    Includes: ".join(";", @includes)."\n";
        %objIncludes=(%objIncludes, map{$_=>1} @includes);
      }
    }

    if (defined($optionsByObject{$object}))
    {
      %objOptions=%{$optionsByObject{$object}};
#      @options=removeCommon(\@commonOptions, \@options);
      if (%objOptions > 0)
      {
        print "    Options: ".join(";", keys %objOptions)."\n";
        %options=(%options, %objOptions);
      }
    }
  }

  print(" Composite Defines: ".join(" ", keys %objDefines)."\n");
  print(" Composite Includes: ".join(" ", keys %objIncludes)."\n");
  print(" Composite Options: ");
  foreach my $key (keys %options)
  {
    print("$key=$options{$key} ");
  }

  print " Resources\n";
  my %desDefines;
  foreach my $res (@resources)
  {
    print "  $res: ".getSource($res)."\n";
    my @defines=@{$definesByObject{$res}};
#    @defines=removeCommon(\@commonDefines, \@defines);
    if (@defines > 0)
    {
      print "    Defines: ".join(";", @defines)."\n";
      foreach my $define (@defines)
      {
        $resDefines{$define}=1;
      }
    }
  }
  print " Libs:\n";
  foreach my $lib (@libs)
  {
    $lib=File::Spec->canonpath($lib);
    print "  $lib\n";
    if ($lib =~ /^..\\lib\\(.*).lib$/)
    {
      push(@afters, "$1");
    }
    if ($lib =~ /^..\\bin\\(.*).lib$/)
    {
      push(@afters, "$1");
    }
  }

  if ($generateMPC)
  {
    $exec =~ /([^.]*)\..*/;
    my $basename = basename($exec, (".dll", ".exe"));
    print("exec=$exec, basename=$basename\n") if ($debug);

    open(MPC, ">", "$dir/$basename.mpc") or die ("Unable to open $dir/$basename.mpc file: $!");
    print(MPC "project($basename) {\n");
    if ($isExe)
    {
      print(MPC "  exename=$basename\n");
    }
    else
    {
      print(MPC "  sharedname=$basename\n");
    }

    print(MPC "\n");

    # specify prerequisites
    foreach my $after (@afters)
    {
      print(MPC "  after += $after\n");
    }

    print(MPC "\n");

    foreach my $define (sort keys %objDefines)
    {
      $define =~ s/ /\\ /g;
      $define =~ s/"//g;
      # Make sure that DEBUG is only in the Debug configurarion
      if ($define =~ /^_DEBUG$/)
      {
        print(MPC "  specific {\n");
        print(MPC "    Debug::macros += $define\n");
        print(MPC "  }\n");
      }
      else
      {
        print(MPC "  macros += $define\n");
      }
    }
    foreach my $define (sort keys %resDefines)
    {
      $define =~ s/ /\\ /g;
      $define =~ s/"//g;
      print(MPC "  macros += $define\n");
    }

    foreach my $include (sort keys %objIncludes)
    {
      print(MPC "  includes += $include\n");
    }

    # precompiling requires the boost libraries
    if (defined($options{"/Fp"}))
    {
      print(MPC "  includes += \$(BOOST)\\include\n");
    }

    foreach my $lib (@libs)
    {
      $lib =~ s/\.lib$//;
        print("got $lib\n");
      # Some libraries use a different lib name, translate those here.
      if ($lib =~ /xerces-c_3/)
      {
        print(MPC "  specific {\n");
        print(MPC "    Debug::lit_libs   += $lib\n");
        $lib =~ s/3D$/3/;
        print(MPC "    Release::lit_libs += $lib\n");
        print(MPC "  }\n");
      }
      elsif ($lib =~ /boost_/)
      {
        print(MPC "  specific {\n");
        print(MPC "    Debug::lit_libs   += $lib\n");
        $lib =~ s/-gd-/-/;
        print(MPC "    Release::lit_libs += $lib\n");
        print(MPC "  }\n");
      }
      elsif ($lib =~ /libcoap/)
      {
        print(MPC "  specific {\n");
        print(MPC "    Debug::lit_libs   += $lib\n");
        $lib =~ s/libcoapd/libcoap/;
        print(MPC "    Release::lit_libs += $lib\n");
        print(MPC "  }\n");
      }
      elsif ($lib =~ /tcl86tg/)
      {
        print(MPC "  specific {\n");
        print(MPC "    Debug::lit_libs   += $lib\n");
        $lib =~ s/tcl86tg/tcl86t/;
        print(MPC "    Release::lit_libs += $lib\n");
        print(MPC "  }\n");
      }
      elsif ($lib =~ /sqlapid/)
      {
        print(MPC "  specific {\n");
        print(MPC "    Debug::lit_libs   += $lib\n");
        $lib =~ s/sqlapid/sqlapi/;
        print(MPC "    Release::lit_libs += $lib\n");
        print(MPC "  }\n");
      }
      else
      {
        print(MPC "  lit_libs += $lib\n");
      }
    }

    if (defined($options{"/Fp"}))
    {
      print(MPC "  pch_header = precompiled.h\n");
      print(MPC "  pch_source = ../precompiled.cpp\n");
    }

    print(MPC "\n  Source_Files {\n");
    if (defined($options{"/Fp"}))
    {
      print(MPC "    ../precompiled.cpp\n");
    }
    foreach my $object (@objects)
    {
      print(MPC "    ".getSource($object)."\n");
    }
    print(MPC "  }\n\n");

    if (-d "$dir/include")
    {
      print(MPC "\n  Header_Files {\n");
      my @headerFiles = glob("$dir/include/*.h");
      foreach my $headerFile (@headerFiles)
      {
        print(MPC "    $headerFile\n");
      }
      print(MPC "  }\n\n");
    }

    print(MPC "  Resource_Files {\n");
    foreach my $res (@resources)
    {
      print(MPC "    ".getSource($res)."\n");
    }
    print(MPC "  }\n\n");

    print(MPC "  specific {\n");
    print(MPC "    postbuild= \\ns {\n");
    print(MPC "if not exist \$(SolutionDir)bin mkdir \$(SolutionDir)bin\n");
    print(MPC "xcopy /d/y/f \$(OutDir)\$(TargetFileName) \$(SolutionDir)bin\n");
    if (!$isExe)
    {
      print(MPC "if not exist \$(SolutionDir)lib mkdir \$(SolutionDir)lib\n");
      print(MPC "if exist \$(OutDir)\$(TargetName).lib xcopy /d/y/f \$(OutDir)\$(TargetName).lib \$(SolutionDir)lib\n");
      print(MPC "if exist \$(OutDir)\$(TargetName).lib xcopy /d/y/f \$(OutDir)\$(TargetName).lib \$(SolutionDir)bin\n");
    }
    if ($basename eq "ctibase")
    {
      print(MPC "..\\..\\yukon-build\\server-build\\BUILD.EXE -f \$(SolutionDir)\\libraries.mak COMPILEBASE=\$(SolutionDir) YUKONOUTPUT=\$(SolutionDir)\\bin all CONFIGURATION=\$(Configuration)\n");
    }
    if ($basename eq "ctiprot")
    {
      print(MPC "if not exist \$(SolutionDir)bin mkdir \$(SolutionDir)bin\n");
      print(MPC "xcopy /d/y/f \$(SolutionDir)Protocol\\3rdParty\\SAprotocol.dll \$(SolutionDir)bin\n");
    }
    print(MPC "    }\n");
    print(MPC "  }\n\n");
    print(MPC "  specific {\n");
    print(MPC "    Release::lib_modifier =\n");
    print(MPC "    Debug::lib_modifier =\n");
    print(MPC "  }\n\n");
    if (defined($options{"/FI"}))
    {
      print("Setting Forced Include for $options{'/FI'}\n") if ($debug);
      print(MPC "  specific {\n");
      print(MPC "    ForcedIncludeFiles=$options{'/FI'}\n");
      print(MPC "  }\n\n");
    }

    print(MPC "}\n\n");

    close(MPC);

    print(MWC "    $dir/$basename.mpc\n");
  }

}

# The option may or may not have a value immediatly following.  If
# there is, grab it, if not, pop one.
sub parseOrShift($$)
{
  my $param=shift;
  my $parms=shift;

  if ( !defined($param) or $param eq "")
  {
    return shift @$parms;
  }

  return $param;
}

sub findCommon($@)
{
  my ($list, $objects)=@_;
  my %list=%$list;
  my @objects=@$objects;

  my %defines;

  # Get the first list
  my $first=shift @objects;
  if (!defined($list{$first}))
  {
    return;
  }
  my @firstDefines=@{$list{$first}};
  %defines=map{$_=>1} @firstDefines;
  print "defines = ".join(" ", keys %defines)."\n" if ($debug);

  foreach my $object (@objects)
  {
    print "object $object\n" if ($debug);
    my @nextDefine=@$list{$object};

    print "next defines = ".join(" ", @nextDefine)."\n" if ($debug);
    my @found=grep($defines{$_}, @nextDefine);
    print "found = ".join(" ", @found)."\n" if ($debug);
    %defines=map{$_=>1} @firstDefines;
    print "defines = ".join(" ", keys %defines)."\n" if ($debug);
  }

  return keys %defines;
}

# Remove values in list1 from list2
sub removeCommon(\@\@)
{
  my ($list1, $list2)=@_;

  foreach my $item (@$list1)
  {
    my $itemEscaped=quotemeta($item);
    @$list2 = grep (!/$itemEscaped/, @$list2);
  }

  return @$list2;
}

sub resolvePath($)
{
  my $path=shift;
  $path =~ s/.*\/common\/include/\..\/common\/include/g;
  $path =~ s/.*\/environment\/libraries\/apache\/apr\/[^\/]*\/include/\$(APR)\/include/g;
  $path =~ s/.*\/environment\/libraries\/apache\/apr-util\/[^\/]*\/include/\$(APRUTIL)\/include/g;
  $path =~ s/.*\/environment\/libraries\/apache\/apr-iconv\/[^\/]*\/include/\$(APRICONV)\/include/g;
  $path =~ s/.*\/environment\/libraries\/apache\/activemq-cpp\/[^\/]*\/include/\$(ACTIVEMQ)\/include/g;
  $path =~ s/.*\/environment\/libraries\/apache\/log4cxx\/[^\/]*\/include/\$(LOG4CXX)\/include/g;
  $path =~ s/.*\/environment\/libraries\/apache\/thrift\/[^\/]*\/include/\$(THRIFT)\/include/g;
  $path =~ s/.*\/environment\/libraries\/apache\/xerces-c\/[^\/]*\/include/\$(XERCES)\/include/g;
  $path =~ s/.*\/environment\/libraries\/boost\/[^\/]*\/include/\$(BOOST)\/include/g;
  $path =~ s/.*\/environment\/libraries\/sqlapi\/[^\/]+\/include/\$(SQLAPI)\/include/g;
  $path =~ s/.*\/environment\/libraries\/openssl\/[^\/]+\/include/\$(OPENSSL)\/include/g;
  $path =~ s/.*\/environment\/libraries\/tcl\/[^\/]+\/include/\$(TCL)\/include/g;
  $path =~ s/.*\/environment\/libraries\/microsoft\/dbghelp\/[^\/]+\/include/\$(DBGHELP)\/include/g;
  $path =~ s/.*\/environment\/libraries\/cajun\/[^\/]+\/include/\$(CAJUN)\/include/g;
  $path =~ s/.*\/environment\/libraries\/libcoap\/[^\/]+\/include/\$(LIBCOAP)\/include/g;

  $path =~ s/.*\/yukon-server\/lib\/saprotocol.lib/..\/Protocol\/3rdParty\/saprotocol.lib/g;
  $path =~ s/.*\/yukon-server\/lib\/apiclilib.lib/..\/Fdr\/Telegyr\/lib\/apiclilib.lib/g;
  $path =~ s/.*\/yukon-server\/lib\/pllib.lib/..\/Fdr\/Telegyr\/lib\/pllib.lib/g;
  $path =~ s/.*\/yukon-server\/lib\/psapi.lib/..\/Fdr\/Telegyr\/lib\/psapi.lib/g;
  $path =~ s/.*\/yukon-server\/lib\/dclnd.lib/..\/Fdr\/Cygnet\/dclnd.lib/g;
  $path =~ s/.*\/yukon-server/../g;

  $path =~ s/.*\/environment\/libraries\/boost\/[^\/]+\/lib/\$(BOOST)\/lib/g;
  $path =~ s/.*\/environment\/libraries\/sqlapi\/[^\/]+\/lib/\$(SQLAPI)\/lib/g;
  $path =~ s/.*\/environment\/libraries\/apache\/activemq-cpp\/[^\/]+\/Debug|Release\/lib/\$(ACTIVEMQ)\/\$(CONFIGURATION)/g;
  $path =~ s/.*\/environment\/libraries\/apache\/apr\/[^\/]+\/Debug|Release\/lib/\$(APR)\/\$(CONFIGURATION)/g;
  $path =~ s/.*\/environment\/libraries\/apache\/apr_util\/[^\/]+\/Debug|Release\/lib/\$(APRUTIL)\/\$(CONFIGURATION)/g;
  $path =~ s/.*\/environment\/libraries\/apache\/apr_iconv\/[^\/]+\/Debug|Release\/lib/\$(APRICONV)\/\$(CONFIGURATION)/g;
  $path =~ s/.*\/environment\/libraries\/apache\/thrift\/[^\/]+\/Debug|Release\/lib/\$(THRIFT)\/\$(CONFIGURATION)/g;
  $path =~ s/.*\/environment\/libraries\/apache\/log4cxx\/[^\/]+\/Debug|Release\/lib/\$(LOG4CXX)\/\$(CONFIGURATION)/g;
  $path =~ s/.*\/environment\/libraries\/apache\/xerces-c\/[^\/]+\/Debug|Release\/lib/\$(XERCES)\/\$(CONFIGURATION)/g;
  $path =~ s/.*\/environment\/libraries\/openssl\/[^\/]+\/Debug|Release\/lib/\$(OPENSSL)\/\$(CONFIGURATION)/g;
  $path =~ s/.*\/environment\/libraries\/microsoft\/dbghelp\/[^\/]+\/lib/\$(DBGHELP)\/lib/g;
  $path =~ s/.*\/environment\/libraries\/libcoap\/[^\/]+\/Debug|Release\/lib/\$(LIBCOAP)\/\$(CONFIGURATION)/g;
  $path =~ s/.*\/environment\/libraries\/tcl\/[^\/]+\/Debug|Release/\$(TCL)\/\$(CONFIGURATION)/g;

  return $path;
}

my %sources;
# Sometimes the source is case insensative
sub getSource($)
{
  my $object=lc(shift);
  my $source=$sources{$object};
  print("Got source for $object as $source\n") if ($debug);
  return $source;
}

sub addSource($$)
{
  my ($object, $source)=@_;
  print("Setting source for $object as $source\n") if ($debug);
  $sources{lc($object)}=$source;
}
