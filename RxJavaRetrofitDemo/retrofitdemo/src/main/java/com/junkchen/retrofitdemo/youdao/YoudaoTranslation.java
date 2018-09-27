package com.junkchen.retrofitdemo.youdao;

import java.util.List;

public class YoudaoTranslation {
    private int errorCode;
    private String query;
    private List<String> translation;
    private Basic basic;


    private class Basic {
//        private String phonetic;
//        private String uk-phonetic;
//        private String us-phonetic;
//        private String uk-speech;
//        private String us-speech;
    }
    /*
{
  "errorCode":"0",
  "query":"good", //查询正确时，一定存在
  "translation": [ //查询正确时一定存在
      "好"
  ],
  "basic":{ // 有道词典-基本词典,查词时才有
      "phonetic":"gʊd"
      "uk-phonetic":"gʊd" //英式音标
      "us-phonetic":"ɡʊd" //美式音标
      "uk-speech": "XXXX",//英式发音
      "us-speech": "XXXX",//美式发音
      "explains":[
          "好处",
          "好的"
          "好"
      ]
  },
  "web":[ // 有道词典-网络释义，该结果不一定存在
      {
          "key":"good",
          "value":["良好","善","美好"]
      },
      {...}
  ]
  ],
  "dict":{
      "url":"yddict://m.youdao.com/dict?le=eng&q=good"
  },
  "webdict":{
      "url":"http://m.youdao.com/dict?le=eng&q=good"
  },
  "l":"EN2zh-CHS",
  "tSpeakUrl":"XXX",//翻译后的发音地址
  "speakUrl": "XXX" //查询文本的发音地址
}
    * */
}
