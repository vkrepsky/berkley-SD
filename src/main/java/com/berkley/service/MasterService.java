package com.berkley.service;

import com.berkley.Util;
import com.berkley.domain.Time;
import com.berkley.singleton.InstanceTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class MasterService {

    private RestTemplate restTemplate;

    @Autowired
    public MasterService() {
        this.restTemplate = createRestTemplateWithTimeout(3000);
    }

    private RestTemplate createRestTemplateWithTimeout(int timeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        return new RestTemplate(factory);
    }

    /**
     * pega o tempo de todas as instâncias, calcula a média e atualiza todas elas
     */
    public void synchronizeTime() {
        Util.log("Master está sincronizando o tempo");

        int[] nodeIndices = Util.nodeSwitch();
        List<LocalDateTime> timeList = new ArrayList<>();

        for (int index : nodeIndices) {
            LocalDateTime nodeTime = fetchNodeTime(index);
            if (nodeTime != null) {
                timeList.add(nodeTime);
            }
        }

        timeList.add(InstanceTime.getInstance().getTime());

        Util.log("Tempo antigo: " + InstanceTime.getInstance().getTime());

        LocalDateTime averageTime = calculateAverage(timeList);
        InstanceTime.setTime(averageTime);

        Util.log("Tempo sincronizado: " + averageTime);

        broadcastTimeUpdate(nodeIndices, averageTime);
    }

    /**
     * faz uma requisição do tempo de uma instancia
     * @param nodeIndex indice da instancia
     * @return clock da instancia ou nulo
     */
    private LocalDateTime fetchNodeTime(int nodeIndex) {
        String url = "http://app" + nodeIndex + ":8080/time";
        try {
            Time nodeResponse = restTemplate.getForEntity(url, Time.class).getBody();
            return nodeResponse.getTime();
        } catch (Exception e) {
            Util.log("Erro ao sincronizar com a instância: " + nodeIndex + ". Ignorando tempo desta instância");
            return null;
        }
    }

    /**
     * atualiza todas as instancias com o tempo médio
     * @param nodeIndices array com todos os indices de instancias
     * @param averageTime tempo medio
     */
    private void broadcastTimeUpdate(int[] nodeIndices, LocalDateTime averageTime) {
        for (int index : nodeIndices) {
            String url = "http://app" + index + ":8080/time";
            try{
                restTemplate.postForEntity(url, new Time(averageTime), String.class);
            }catch (Exception e){
                Util.log("Erro ao atualizar tempo da instância: " + index + ". Instancia provavelmente esta inativa");
            }
        }
    }

    /**
     * calcula tempo médio
     * @param localDateTimes tempo de todas as instâncias
     * @return uma média do tempo das instâncias
     */
    private LocalDateTime calculateAverage(List<LocalDateTime> localDateTimes) {
        if (localDateTimes == null || localDateTimes.isEmpty()) {
            throw new IllegalArgumentException("Lista não pode ser nula");
        }

        // Converte cada LocalDateTime para Duration
        long totalMillis = 0;
        for (LocalDateTime dateTime : localDateTimes) {
            totalMillis += dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        // Calcula avg
        long averageMillis = totalMillis / localDateTimes.size();

        // Converte denovo para LocalDateTime
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(averageMillis), ZoneId.systemDefault());
    }
}
