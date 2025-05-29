package com.ssg.order.global.core.config;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JasyptConfigTest {
    @Autowired
    @Qualifier("jasyptEncryptorBean")
    private StringEncryptor stringEncryptor;

    @Test
    public void generatePassword() {
        String encrypt = stringEncryptor.encrypt("");
        System.out.println(encrypt);
        String decrypt = stringEncryptor.decrypt(encrypt);
        System.out.println(decrypt);
    }
}