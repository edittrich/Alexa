https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/alexa-skills-kit-interface-reference
https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/developing-an-alexa-skill-as-a-web-service
https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/deploying-a-sample-skill-as-a-web-service

http://www.thehecklers.org/2017/03/06/create-alexa-skill-spring-boot-cf/
https://raw.githubusercontent.com/qaware/iot-hessen-amazon-echo/master/slides.pdf

curl -v -d "@alexa.json" -H "Content-Type: application/json" https://edittrich.de:8443/alexa
alexa.json
{
  "session": {
    "new": false,
    "sessionId": "SessionId.a109c7c4-c005-4104-9d8b-9c545acbbac2",
    "application": {
      "applicationId": "amzn1.ask.skill.c8402c05-714f-4fb4-8ad9-17dc9cd7e95c"
    },
    "attributes": {},
    "user": {
      "userId": "amzn1.ask.account.AE7QK3YTRSNRR5KJXORQGPDNCYVRIWZYFTO324AQ2TAQHNEUQHMMUY2T3OPOEGTJU5CODAITA2NH7WWIC5DWBLBNNZABHONQJ5KQP7SS4NDGXPTAJVAQ7MBW7GEFM5DARXQ2DVWWMLO5WTZ52R6BJTHDR7RZPIKPNQDGIQGJGTBVGJFWBO7CLXFKWN2Q4MMGA6GUPPTG535PEVA"
    }
  },
  "request": {
    "type": "IntentRequest",
    "requestId": "EdwRequestId.541607d5-0594-4727-987c-3411d1df2f89",
    "intent": {
      "name": "HelloWorldIntent",
      "slots": {}
    },
    "locale": "en-US",
    "timestamp": "2017-08-12T10:01:32Z"
  },
  "context": {
    "AudioPlayer": {
      "playerActivity": "IDLE"
    },
    "System": {
      "application": {
        "applicationId": "amzn1.ask.skill.c8402c05-714f-4fb4-8ad9-17dc9cd7e95c"
      },
      "user": {
        "userId": "amzn1.ask.account.AE7QK3YTRSNRR5KJXORQGPDNCYVRIWZYFTO324AQ2TAQHNEUQHMMUY2T3OPOEGTJU5CODAITA2NH7WWIC5DWBLBNNZABHONQJ5KQP7SS4NDGXPTAJVAQ7MBW7GEFM5DARXQ2DVWWMLO5WTZ52R6BJTHDR7RZPIKPNQDGIQGJGTBVGJFWBO7CLXFKWN2Q4MMGA6GUPPTG535PEVA"
      },
      "device": {
        "supportedInterfaces": {}
      }
    }
  },
  "version": "1.0"
}