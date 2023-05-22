// Copyright 2021 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.bazel.bzlmod;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;

/** Object to hold all module extensions related data needed in the lockfile*/
@AutoValue
public abstract class LockfileModuleExtension {

  public abstract ImmutableByteArray getTransitiveDigest();

  /** All usages of this extension, by the key of the module where the usage occurs. */
  public abstract ImmutableMap<ModuleKey, ModuleExtensionUsage> getExtensionUsages();

  public abstract ImmutableMap<String, RepoSpec> getGeneratedRepoSpecs();

  public static LockfileModuleExtension create(ImmutableByteArray transitiveDigest,
      ImmutableMap<ModuleKey, ModuleExtensionUsage> extensionUsages,
      ImmutableMap<String, RepoSpec> generatedRepoSpecs){
    return new AutoValue_LockfileModuleExtension(
        transitiveDigest, extensionUsages, generatedRepoSpecs);
  }

  public ArrayList<String> getExtensionDiff(String extensionName, byte[] transitiveDigest,
      ImmutableMap<ModuleKey, ModuleExtensionUsage> extensionUsages) {
    ArrayList<String> extDiff = new ArrayList<>();
    if (!Arrays.equals(transitiveDigest, getTransitiveDigest().getData())) {
      extDiff.add("The implementation of the extension named '" + extensionName + "' or one of its transitive modules have changed");
    }
    if (!extensionUsages.equals(getExtensionUsages())) {
      extDiff.add("The usages of the extension named '" + extensionName + "' has changed");
    }
    return extDiff;
  }
}
