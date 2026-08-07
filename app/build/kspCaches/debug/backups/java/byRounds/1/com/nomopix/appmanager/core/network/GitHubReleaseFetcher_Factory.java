package com.nomopix.appmanager.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class GitHubReleaseFetcher_Factory implements Factory<GitHubReleaseFetcher> {
  @Override
  public GitHubReleaseFetcher get() {
    return newInstance();
  }

  public static GitHubReleaseFetcher_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GitHubReleaseFetcher newInstance() {
    return new GitHubReleaseFetcher();
  }

  private static final class InstanceHolder {
    private static final GitHubReleaseFetcher_Factory INSTANCE = new GitHubReleaseFetcher_Factory();
  }
}
