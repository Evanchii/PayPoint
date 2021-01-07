package xyz.paypnt.paypoint;

import java.util.HashMap;

public class DriverRegisterSetterAndGetter {
    private HashMap<String,Object> driverRegister=new HashMap();

    public DriverRegisterSetterAndGetter() {

    }

    @Override
    public String toString() {
        return "DriverRegisterSetterAndGetter{" +
                "driverRegister=" + driverRegister +
                '}';
    }

    public DriverRegisterSetterAndGetter(HashMap<String, Object> driverRegister) {
        this.driverRegister = driverRegister;
    }

    public HashMap<String, Object> getDriverRegister() {
        return driverRegister;
    }

    public void setDriverRegister(HashMap<String, Object> driverRegister) {
        this.driverRegister = driverRegister;
    }
}
