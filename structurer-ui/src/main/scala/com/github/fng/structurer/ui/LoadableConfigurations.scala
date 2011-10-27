package com.github.fng.structurer.ui

import instrument.LoadableClasspathConfigMenuItem
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.support.{ResourcePatternResolver, ResourcePatternUtils}


trait LoadableConfigurations {


  private lazy val resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader())
  private lazy val resources = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "**/*.json")
  lazy val loadableClasspathConfigurations: List[LoadableClasspathConfigMenuItem] = resources.map {
    resource =>
      new LoadableClasspathConfigMenuItem(resource.getFilename, resource)
  }.toList


}