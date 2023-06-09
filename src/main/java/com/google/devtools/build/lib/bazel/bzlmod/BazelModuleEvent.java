package com.google.devtools.build.lib.bazel.bzlmod;

import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.events.ExtendedEventHandler;
import com.google.devtools.build.lib.vfs.Path;

public interface BazelModuleEvent extends ExtendedEventHandler.Postable{

  Path getRootDirectory();

  String getModuleFileHash();

  BzlmodFlagsAndEnvVars getFlags();

  ImmutableMap<String, String> getLocalOverrideHashes();

  ImmutableMap<ModuleKey, Module> getModuleDepGraph();
}
