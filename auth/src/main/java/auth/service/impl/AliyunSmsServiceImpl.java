package auth.service.impl;

import auth.service.SmsService;
import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class AliyunSmsServiceImpl implements SmsService {

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.sign-name}")
    private String signName;

    @Value("${aliyun.sms.template-code}")
    private String templateCode;

    @Value("${aliyun.sms.enabled}")
    private Boolean enabled;

    private Client client;

    @PostConstruct
    public void init() throws Exception {
        if (enabled && accessKeyId != null && !accessKeyId.isEmpty()) {
            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setEndpoint("dysmsapi.aliyuncs.com");
            this.client = new Client(config);
            log.info("阿里云短信服务初始化成功");
        } else {
            log.warn("阿里云短信服务未启用，将使用模拟发送模式");
        }
    }

    @Override
    public boolean sendVerificationCode(String phoneNumber, String code) {
        if (!enabled || client == null) {
            log.info("【模拟发送短信】手机号: {}, 验证码: {}", phoneNumber, code);
            return true;
        }

        try {
            SendSmsVerifyCodeRequest sendSmsRequest = new SendSmsVerifyCodeRequest()
                    .setPhoneNumber(phoneNumber)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam("{\"code\":\"" + code + "\",\"min\":\"5\"}");
            RuntimeOptions runtimeOptions = new RuntimeOptions();
            runtimeOptions.setReadTimeout(10000);
            runtimeOptions.setAutoretry(true);
            SendSmsVerifyCodeResponse response = client.sendSmsVerifyCodeWithOptions(sendSmsRequest,runtimeOptions);
            
            if ("OK".equals(response.getBody().getCode())) {
                log.info("短信发送成功，手机号: {}", phoneNumber);
                return true;
            } else {
                log.error("短信发送失败，手机号: {}, 错误码: {}, 错误信息: {}",
                        phoneNumber, 
                        response.getBody().getCode(),
                        response.getBody().getMessage());
                return false;
            }
        } catch (Exception e) {
            log.error("短信发送异常，手机号: {}", phoneNumber, e);
            return false;
        }
    }
}
