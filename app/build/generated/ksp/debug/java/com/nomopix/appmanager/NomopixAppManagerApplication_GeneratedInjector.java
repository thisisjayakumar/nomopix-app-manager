package com.nomopix.appmanager;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;
import javax.annotation.processing.Generated;

@OriginatingElement(
    topLevelClass = NomopixAppManagerApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
@Generated("dagger.hilt.android.processor.internal.androidentrypoint.InjectorEntryPointGenerator")
public interface NomopixAppManagerApplication_GeneratedInjector {
  void injectNomopixAppManagerApplication(
      NomopixAppManagerApplication nomopixAppManagerApplication);
}
