package com.berkley.singleton;

import java.util.Objects;

/**
 * Singleton responsavel por saber quem é o mestre atual e o ID da propria instância
 * e alterar valores destas variáveis
 */
public class InstanceInfo {

    private static InstanceInfo instance;
    private boolean master;
    private Integer instanceId; //Id desta instancia
    private Integer masterInstance;

    private InstanceInfo() {
    }
    public static InstanceInfo getInstance() {
        if (instance == null) {
            instance = new InstanceInfo();
        }
        return instance;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public Integer getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Integer id) {
        this.instanceId = id;
    }

    public Integer getMasterInstance() {
        return masterInstance;
    }

    public void setMasterInstance(Integer masterInstance) {
        setMaster(Objects.equals(masterInstance, instanceId));
        this.masterInstance = masterInstance;
    }
}
