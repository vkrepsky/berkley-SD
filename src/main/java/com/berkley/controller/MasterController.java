package com.berkley.controller;

import com.berkley.domain.Master;
import com.berkley.singleton.InstanceInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/master")
public class MasterController {

    /**
     *
     * @return qual é o master atual ou NO_CONTENT se não estiver setado
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getMaster(){
        var id = InstanceInfo.getInstance().getMasterInstance();

        if (id == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(new Master(id));
    }
}
