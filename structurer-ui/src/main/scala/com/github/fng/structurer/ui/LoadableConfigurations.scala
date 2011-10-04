package com.github.fng.structurer.ui

import instrument.LoadableConfigMenuItem
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.support.{ResourcePatternResolver, ResourcePatternUtils}


trait LoadableConfigurations {


  private lazy val resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader())
  private lazy val resources = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "**/*.json")
  lazy val loadableConfigurations: List[LoadableConfigMenuItem] = resources.map {
    resource =>
      new LoadableConfigMenuItem(resource.getFilename, resource)
  }.toList


}