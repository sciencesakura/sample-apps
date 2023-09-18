package com.sciencesakura.sample;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(importOptions = DoNotIncludeTests.class)
class ArchitectureTest {

  @ArchTest
  static ArchRule layer_rule = layeredArchitecture()
      .consideringOnlyDependenciesInLayers()
      .layer("domain").definedBy("com.sciencesakura.sample.domain..")
      .layer("infrastructure").definedBy("com.sciencesakura.sample.infra..")
      .layer("presentation").definedBy("com.sciencesakura.sample.api..")
      .whereLayer("domain").mayNotAccessAnyLayer()
      .whereLayer("infrastructure").mayOnlyAccessLayers("domain")
      .whereLayer("infrastructure").mayNotBeAccessedByAnyLayer()
      .whereLayer("presentation").mayOnlyAccessLayers("domain")
      .whereLayer("presentation").mayNotBeAccessedByAnyLayer();
}
