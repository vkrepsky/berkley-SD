package com.berkley;


import com.berkley.singleton.InstanceInfo;

public class Util {
    /**
     *
     * @return array com IDs de outras Instâncias
     */
    public static int[] nodeSwitch(){

        switch (InstanceInfo.getInstance().getInstanceId()){
            case 1:
                return new int[]{2, 3};
            case 2:
                return new int[]{1,3};
            case 3:
                return new int[]{2,1};
        }
        throw new RuntimeException("Parametro inválido");
    }

    /**
     * escreve logs especificando qual instancia esta logando
     * @param msg a mensagem a logar
     */
    public static void log(String msg) {
        InstanceInfo instance = InstanceInfo.getInstance();
        String role = instance.isMaster() ? "MASTER" : "WORKER";

        System.out.println("[INSTANCIA: " + instance.getInstanceId() + " | ROLE: " + role + "] " + msg);
    }

}
