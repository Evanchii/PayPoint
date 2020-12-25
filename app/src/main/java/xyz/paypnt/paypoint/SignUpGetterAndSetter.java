package xyz.paypnt.paypoint;

import java.util.HashMap;

public class SignUpGetterAndSetter {
    private HashMap<String,Object> signUp=new HashMap<>();

    public SignUpGetterAndSetter(HashMap<String, Object> signUp) {
        this.signUp = signUp;
    }

    public SignUpGetterAndSetter() {

    }

    @Override
    public String toString() {
        return "SignUpGetterAndSetter{" +
                "signUp=" + signUp +
                '}';
    }

    public HashMap<String, Object> getSignUp() {
        return signUp;
    }

    public void setSignUp(HashMap<String, Object> signUp) {
        this.signUp = signUp;
    }
}
