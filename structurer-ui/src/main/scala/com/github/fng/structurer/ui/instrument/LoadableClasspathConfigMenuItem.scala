package com.github.fng.structurer.ui.instrument

import swing.MenuItem
import org.springframework.core.io.Resource
import java.io.File

class LoadableClasspathConfigMenuItem(title: String, val resource: Resource) extends MenuItem(title){

}

class LoadableFileConfigMenuItem(title: String, val file: File) extends MenuItem(title){

}