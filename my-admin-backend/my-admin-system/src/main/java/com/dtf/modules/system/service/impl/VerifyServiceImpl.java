package com.dtf.modules.system.service.impl;

import com.dtf.exception.BadRequestException;
import com.dtf.modules.system.service.VerifyService;
import com.dtf.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/26 0:14
 */
@Service
@RequiredArgsConstructor
public class VerifyServiceImpl implements VerifyService {
    @Value("${code.expiration}")
    private Long expiration;
    private final RedisUtils redisUtils;

    @Override
    public void validated(String key, String code) {
        Object value = redisUtils.get(key);
        if (value == null || !value.toString().equals(code)) {
            throw new BadRequestException("无效验证码");
        }
        redisUtils.del(key);
    }
}
