package com.github.fng.structurer.config

import org.specs2.mutable._
import org.springframework.core.io.ClassPathResource
import com.efgfp.commons.spring.resource.ResourceLoader

class ProductConfigSpec extends Specification {

  "The 'Product Config'" should {
    "be loaded from diks and parsed" in {
      val productConfig = ProductConfig(ResourceLoader.loadStringResourceUtf8(
        new ClassPathResource("config/barrier-reverse-convertible-notional.json")))
      productConfig.productTypeId must equalTo("340.001")
    }
    "be converted to Json" in {
      val productConfig = ProductConfig("123.456", "Bullish", "Notional", "SingleUnderlying", "false", "Test Allotment", Nil, Nil, Nil)
      val json = productConfig.toJson
      json must be equalTo("""{"product-config" : {"allotment" : "Test Allotment", "payoffType" : "Bullish", "field" : [], "underlyingType" : "SingleUnderlying", "option" : [], "productTypeId" : "123.456", "autocallable" : "false", "bond" : [], "quotationType" : "Notional"}}""")
    }
    "handle parsing from and printing to json correctly" in {
      val productConfig = ProductConfig(ResourceLoader.loadStringResourceUtf8(
        new ClassPathResource("config/barrier-reverse-convertible-notional.json")))

      val json = productConfig.toJson
      
      ProductConfig(json) must be equalTo(productConfig)
    }
  }
}