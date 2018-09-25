package com.junkchen.okhttpdemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by Junk on 2017/8/3.
 */

public class FastJsonTest {
    public static void main(String[] args) {

        String jsonStr = "{\"result\":true,\"countStatus\":0}";
        Object parse = JSON.parse(jsonStr);
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        Boolean result = jsonObject.getBoolean("result");
        int countStatus = jsonObject.getIntValue("countStatus");
        String userId = jsonObject.getString("userId");
        System.out.println("result: " + result + ", countStatus: " + countStatus + ", userId: " + userId);

        User user = new User("Junk Chen", 18, true);
        String jsonString = JSON.toJSONString(user);
        System.out.println(jsonString);
//        JSONObject object = JSON.parseObject(jsonString);
        User user2 = JSON.parseObject(jsonString, User.class);
        System.out.println(user2.toString());
    }

    public static class User {
        public String name;
        public int age;
        public boolean isMale;

        public User(String name, int age, boolean isMale) {
            this.name = name;
            this.age = age;
            this.isMale = isMale;
        }

        public User() {
        }

        @Override
        public String toString() {
            return User.class.getSimpleName() + ": name = " + name + ", age = " + age + ", isMale = " + isMale;
        }
    }
}
