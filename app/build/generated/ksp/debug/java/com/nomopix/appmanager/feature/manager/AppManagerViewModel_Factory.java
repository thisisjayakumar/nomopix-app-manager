package com.nomopix.appmanager.feature.manager;

import com.nomopix.appmanager.core.installer.ApkInstallerManager;
import com.nomopix.appmanager.core.network.GitHubReleaseFetcher;
import com.nomopix.appmanager.core.network.IndexParser;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AppManagerViewModel_Factory implements Factory<AppManagerViewModel> {
  private final Provider<IndexParser> indexParserProvider;

  private final Provider<GitHubReleaseFetcher> releaseFetcherProvider;

  private final Provider<ApkInstallerManager> installerManagerProvider;

  public AppManagerViewModel_Factory(Provider<IndexParser> indexParserProvider,
      Provider<GitHubReleaseFetcher> releaseFetcherProvider,
      Provider<ApkInstallerManager> installerManagerProvider) {
    this.indexParserProvider = indexParserProvider;
    this.releaseFetcherProvider = releaseFetcherProvider;
    this.installerManagerProvider = installerManagerProvider;
  }

  @Override
  public AppManagerViewModel get() {
    return newInstance(indexParserProvider.get(), releaseFetcherProvider.get(), installerManagerProvider.get());
  }

  public static AppManagerViewModel_Factory create(Provider<IndexParser> indexParserProvider,
      Provider<GitHubReleaseFetcher> releaseFetcherProvider,
      Provider<ApkInstallerManager> installerManagerProvider) {
    return new AppManagerViewModel_Factory(indexParserProvider, releaseFetcherProvider, installerManagerProvider);
  }

  public static AppManagerViewModel newInstance(IndexParser indexParser,
      GitHubReleaseFetcher releaseFetcher, ApkInstallerManager installerManager) {
    return new AppManagerViewModel(indexParser, releaseFetcher, installerManager);
  }
}
