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
public final class IndexParser_Factory implements Factory<IndexParser> {
  @Override
  public IndexParser get() {
    return newInstance();
  }

  public static IndexParser_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static IndexParser newInstance() {
    return new IndexParser();
  }

  private static final class InstanceHolder {
    private static final IndexParser_Factory INSTANCE = new IndexParser_Factory();
  }
}
