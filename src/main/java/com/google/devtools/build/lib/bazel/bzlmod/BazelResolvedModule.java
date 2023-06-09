package com.google.devtools.build.lib.bazel.bzlmod;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;
import com.google.common.flogger.GoogleLogger;
import com.google.devtools.build.lib.bazel.bzlmod.BazelLockFileFunction.BazelLockfileFunctionException;
import com.google.devtools.build.lib.bazel.repository.RepositoryOptions;
import com.google.devtools.build.lib.bazel.repository.RepositoryOptions.LockfileMode;
import com.google.devtools.build.lib.bazel.repository.RepositoryResolvedOptions;
import com.google.devtools.build.lib.runtime.BlazeModule;
import com.google.devtools.build.lib.runtime.Command;
import com.google.devtools.build.lib.runtime.CommandEnvironment;
import com.google.devtools.common.options.OptionsBase;
import java.util.ArrayList;
import java.util.List;

public class BazelResolvedModule extends BlazeModule {

  private BazelModuleEvent resolvedModule;

  private static final GoogleLogger logger = GoogleLogger.forEnclosingClass();

  @Override
  public Iterable<Class<? extends OptionsBase>> getCommandOptions(Command command) {
    return ImmutableSet.of("sync", "fetch", "build", "query").contains(command.name())
        ? ImmutableList.of(RepositoryResolvedOptions.class)
        : ImmutableList.of();
  }

  @Override
  public void beforeCommand(CommandEnvironment env) {
    RepositoryOptions options =
        env.getOptions().getOptions(RepositoryOptions.class);
    if (!options.lockfileMode.equals(LockfileMode.OFF)) {
      env.getEventBus().register(this);
    }
  }

  @Override
  public void afterCommand() {
    if(resolvedModule == null) { //module didn't change --> no event was posted
      return;
    }
    BazelLockFileValue updatedLockfile = BazelLockFileValue.create(
        BazelLockFileValue.LOCK_FILE_VERSION,
        resolvedModule.getModuleFileHash(),
        resolvedModule.getFlags(),
        resolvedModule.getLocalOverrideHashes(),
        resolvedModule.getModuleDepGraph());
    try {
      BazelLockFileFunction.updateLockfile(resolvedModule.getRootDirectory(), updatedLockfile);
    } catch (BazelLockfileFunctionException e) {
      logger.atWarning().withCause(e)
          .log("Error while updating MODULE.bazel.lock file %s", e.getMessage());
    }
    this.resolvedModule = null;
  }

  @Subscribe
  public void resolved(BazelModuleEvent event) {
    this.resolvedModule = event;
  }

}
