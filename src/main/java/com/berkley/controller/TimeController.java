package com.berkley.controller;

import com.berkley.Util;
import com.berkley.domain.Time;
import com.berkley.singleton.InstanceInfo;
import com.berkley.singleton.InstanceTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/time")
public class TimeController {


    /**
     *
     * @return o clock da instância
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Time> getTime(){
        return ResponseEntity.ok().body(new Time(InstanceTime.getInstance().getTime()));
    }

    /**
     *
     * @param time o novo tempo atualizado da istância
     * @return ok
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity setTime(@RequestBody Time time){
        Util.log("Tempo antigo da instancia: "+ InstanceInfo.getInstance().getInstanceId()+": "+ InstanceTime.getInstance().getTime());
        InstanceTime.setTime(time.getTime());
        Util.log("Tempo novo da instancia: "+  InstanceInfo.getInstance().getInstanceId()+": "+ InstanceTime.getInstance().getTime());
        return ResponseEntity.ok().build();
    }
}
