package com.github.fng.structurer.config

import org.springframework.core.io.ClassPathResource
import com.efgfp.commons.spring.resource.ResourceLoader


object ConfigLoaderTest {
 def main(args: Array[String]){
   val productConfig = ProductConfig(ResourceLoader.loadStringResourceUtf8(new ClassPathResource("config/barrier-reverse-convertible-notional.json")))
   println(productConfig)
 }
}