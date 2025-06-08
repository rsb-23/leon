package com.svenjacobs.app.leon.core.domain.sanitizer.allquery

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class AllQuerySanitizerTest : WordSpec({
    val sanitizer = AllQuerySanitizer()

    "invoke" should {
        "remove all parameters from ikea.com URL" {
            val result = sanitizer("https://www.ikea.com/ch/en/p/billy-bookcase-white-30263844/?gad_source=1&extProvId=5")
            result shouldBe "https://www.ikea.com/ch/en/p/billy-bookcase-white-30263844/"
        }
        "remove all parameters from kogan.com URL" {
            val result = sanitizer("https://www.kogan.com/au/buy/heybattery-samsung-galaxy-watch-7-44mm-bluetooth-l310-silver-ss-l310-sv/?ssid=201.bae7632c-5c90-46cf-a071-416f0879ecb5&click_id=VWDA74xN4CIffN9Rb_8l-46p")
            result shouldBe "https://www.kogan.com/au/buy/heybattery-samsung-galaxy-watch-7-44mm-bluetooth-l310-silver-ss-l310-sv/"
        }
    }

    "matchesDomain" should {
        "match for ikea.com" {
            sanitizer.matchesDomain("https://www.ikea.com/ch/en/p/billy-bookcase-white-30263844/") shouldBe true
        }
        "match for kogan.com" {
            sanitizer.matchesDomain("https://www.kogan.com/au/buy/kogan-4k-led-tv/") shouldBe true
        }
        "not match for other domains" {
            sanitizer.matchesDomain("https://www.example.com/product/123") shouldBe false
        }
    }
})
