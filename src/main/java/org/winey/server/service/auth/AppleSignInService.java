package org.winey.server.service.auth;

import com.google.gson.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AppleSignInService {

    @Value("${jwt.APPLE_URL}")
    private String APPLE_URL;


    protected JsonArray getApplePublicKeyList() {
        try {
            System.out.println("여기1");
            URL url = new URL(APPLE_URL);
            System.out.println("여기2");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            System.out.println("여기3");
            connection.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            br.close();

            JsonObject keys = (JsonObject) JsonParser.parseString(result.toString());
            return (JsonArray) keys.get("keys");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected PublicKey makePublicKey(String accessToken, JsonArray publicKeyList) throws NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            JsonObject selectedObject = null;
            System.out.println("lalala");
            String[] decodeArray = accessToken.split("\\.");
            for (String d:decodeArray) {
                System.out.println(d);
            }
            System.out.println("hihi");

            String header = new String(Base64.getDecoder().decode(decodeArray[0].substring(7)));
            System.out.println("alal");

            JsonElement kid = ((JsonObject) JsonParser.parseString(header)).get("kid");
            JsonElement alg = ((JsonObject) JsonParser.parseString(header)).get("alg");
            System.out.println("akak");
            for (JsonElement publicKey : publicKeyList) {
                JsonObject publicKeyObject = publicKey.getAsJsonObject();
                JsonElement publicKid = publicKeyObject.get("kid");
                JsonElement publicAlg = publicKeyObject.get("alg");

                if (Objects.equals(kid, publicKid) && Objects.equals(alg, publicAlg)) {
                    selectedObject = publicKeyObject;
                    break;
                }
            }

            if (selectedObject == null) {
                throw new InvalidKeySpecException("공개키를 찾을 수 없습니다.");
            }

            return getPublicKey(selectedObject);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
//        JsonObject selectedObject = null;
//
//        String[] decodeArray = accessToken.split("\\.");
//        String header = new String(Base64.getDecoder().decode(decodeArray[0].substring(7)));
//
//        JsonElement kid = ((JsonObject) JsonParser.parseString(header)).get("kid");
//        JsonElement alg = ((JsonObject) JsonParser.parseString(header)).get("alg");
//
//        for (JsonElement publicKey : publicKeyList) {
//            JsonObject publicKeyObject = publicKey.getAsJsonObject();
//            JsonElement publicKid = publicKeyObject.get("kid");
//            JsonElement publicAlg = publicKeyObject.get("alg");
//
//            if (Objects.equals(kid, publicKid) && Objects.equals(alg, publicAlg)) {
//                selectedObject = publicKeyObject;
//                break;
//            }
//        }
//
//        if (selectedObject == null) {
//            throw new InvalidKeySpecException("공개키를 찾을 수 없습니다.");
//        }
//
//        return getPublicKey(selectedObject);
        return null;
    }

    private PublicKey getPublicKey(JsonObject object) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String nStr = object.get("n").toString();
        String eStr = object.get("e").toString();

        byte[] nBytes = Base64.getUrlDecoder().decode(nStr.substring(1, nStr.length() -1));
        byte[] eBytes = Base64.getUrlDecoder().decode(eStr.substring(1, eStr.length() -1));

        BigInteger nValue = new BigInteger(1, nBytes);
        BigInteger eValue = new BigInteger(1, eBytes);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(nValue, eValue);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);
    }

    protected String getAppleData(String socialAccessToken) throws NoSuchAlgorithmException, InvalidKeySpecException {
        JsonArray publicKeyList = getApplePublicKeyList();
        System.out.println(publicKeyList);
        PublicKey publicKey = makePublicKey(socialAccessToken, publicKeyList);

        Claims userInfo = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(socialAccessToken.substring(7))
                .getBody();

        JsonObject userInfoObject = (JsonObject) JsonParser.parseString(new Gson().toJson(userInfo));
        return userInfoObject.get("email").getAsString();
    }
}
