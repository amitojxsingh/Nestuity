// TODO: Remove file after testing docker container hot reloading.
package com.nestuity.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World! 18 uwu kitty cat :3 purr purr";
    }
}
