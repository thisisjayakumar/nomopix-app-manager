package com.nomopix.appmanager.core.installer;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ApkInstallerManager_Factory implements Factory<ApkInstallerManager> {
  private final Provider<Context> contextProvider;

  public ApkInstallerManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ApkInstallerManager get() {
    return newInstance(contextProvider.get());
  }

  public static ApkInstallerManager_Factory create(Provider<Context> contextProvider) {
    return new ApkInstallerManager_Factory(contextProvider);
  }

  public static ApkInstallerManager newInstance(Context context) {
    return new ApkInstallerManager(context);
  }
}
