package com.debug.steadyjack.rabbitmq.message;

import com.debug.steadyjack.entity.User;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/9/28.
 */
@Data
@ToString
public class UserRegisterMessage implements Serializable{

    private User user;

    private String url;

    public UserRegisterMessage() {
    }

    public UserRegisterMessage(User user, String url) {
        this.user = user;
        this.url = url;
    }
}