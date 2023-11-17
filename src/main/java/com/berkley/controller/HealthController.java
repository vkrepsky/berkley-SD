package com.berkley.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/h")
public class HealthController {
    /**
     *
     * @return health check para saber se o master está rodando
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(){
        return ResponseEntity.ok().build();
    }
}
