package com.berkley.service;

import com.berkley.Util;
import com.berkley.domain.Master;
import com.berkley.singleton.InstanceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WorkerService {

    private RestTemplate restTemplate;

    @Autowired
    public WorkerService() {
        this.restTemplate = createRestTemplateWithTimeout(5000);
    }

    private RestTemplate createRestTemplateWithTimeout(int timeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        return new RestTemplate(factory);
    }

    /**
     * verifica se o master está ativo. Caso ocorra um timeout de 5 segundos,
     * um novo master será setado
     */
    public void checkMasterHealth() {
        try {
            Util.log("verificando se master com ID: "+getCurrentMasterId()+" está ativo");
            restTemplate.getForEntity("http://app"+getCurrentMasterId()+":8080/h", Master.class);
            Util.log("master com ID: "+getCurrentMasterId()+" permanece ativo");
        }catch (Exception e) {
            Util.log("master não está ativo, selecionando novo master");
            setNewMaster();
        }
    }

    /**
     * seta um novo master. Pega o Id do master antigo e adiciona +1, este é o ID do novo master
     * Se este Id for maior que o ID mais alto da lista (neste caso, estamos trabalhando com 3 instâncias, portanto o maior ID é 3)
     * o Id do novo master se torna 1
     */
    private void setNewMaster(){
        var newMasterId = getCurrentMasterId() + 1;

        if (newMasterId > 3){
            newMasterId = 1;
        }
        Util.log("novo master com ID: "+newMasterId+ " selecionado");
        InstanceInfo.getInstance().setMasterInstance(newMasterId);
    }

    public Integer getCurrentMasterId() {
        return InstanceInfo.getInstance().getMasterInstance();
    }

}
