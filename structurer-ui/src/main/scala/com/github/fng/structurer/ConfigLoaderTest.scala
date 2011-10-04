package com.github.fng.structurer

import config.ProductConfig
import org.springframework.core.io.ClassPathResource
import com.efgfp.commons.spring.resource.ResourceLoader


object ConfigLoaderTest {
 def main(args: Array[String]){
   val productConfig = ProductConfig(ResourceLoader.loadStringResourceUtf8(new ClassPathResource("config/barrier-reverse-convertible-notional.json")))
   println(productConfig)
 }
}