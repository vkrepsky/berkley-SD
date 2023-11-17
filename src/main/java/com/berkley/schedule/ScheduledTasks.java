package com.berkley.schedule;

import com.berkley.Util;
import com.berkley.service.MasterService;
import com.berkley.service.WorkerService;
import com.berkley.singleton.InstanceInfo;
import com.berkley.singleton.InstanceTime;
import com.berkley.singleton.SchedulerState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private MasterService masterService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private SchedulerState schedulerState;

    /**
     * verifica os clocks das instâncias a cada 5 segundos
     */
    @Scheduled(fixedRate = 5000)
    public void syncronize() {
        // Verifica se o agendador foi inicializado
        if (!schedulerState.isInitialized()) {
            return;
        }
        if (InstanceInfo.getInstance().isMaster()){
            masterService.synchronizeTime();
        }
    }

    /**
     * verifica se o master está rodando a cada 15 segundos
     */
    @Scheduled(fixedRate = 15000)
    public void checkHealth() {
        // Verifica se o agendador foi inicializado
        if (!schedulerState.isInitialized()) {
            return;
        }
        if (!InstanceInfo.getInstance().isMaster()){
            workerService.checkMasterHealth();
        }
    }


    /**
     * randomiza os tempos do relogios a cada 20 segundos
     * (Somente para testar a sincronização)
     */
    @Scheduled(fixedRate = 20000)
    public void randomizeTime(){
        // Verifica se o agendador foi inicializado
        if (!schedulerState.isInitialized()) {
            return;
        }
        var ldt = InstanceTime.getInstance().randomizeTime();
        Util.log("randomizando tempo da instancia. Novo tempo: " + ldt);
    }

}
