package com.ew.gerocomium.common.config.security.handler;

import com.ew.gerocomium.common.util.AesUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 自定义加密组件
 */
@Component
public class PasswordEncoderImpl extends AesUtil implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return aesEncode(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return aesMatch(rawPassword.toString(), encodedPassword);
    }
}
