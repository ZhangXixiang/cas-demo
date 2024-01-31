package entity;

import org.apereo.cas.authentication.RememberMeUsernamePasswordCredential;

/**
 * @author Tiger
 */
public class MyCredential extends RememberMeUsernamePasswordCredential {

    private String captcha;

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
